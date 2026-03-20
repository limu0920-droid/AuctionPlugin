package com.mlml.ashlight.auction.utils

import com.mlml.ashlight.auction.core.config.PluginConfig
import com.mlml.ashlight.auction.core.model.AuctionItem
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import java.util.UUID

object Database {

    private lateinit var dataSource: HikariDataSource

    fun connect() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://${PluginConfig.mysqlHost}:${PluginConfig.mysqlPort}/${PluginConfig.mysqlDatabase}?useSSL=false&characterEncoding=utf8&serverTimezone=UTC"
            username = PluginConfig.mysqlUsername
            password = PluginConfig.mysqlPassword
            maximumPoolSize = PluginConfig.mysqlPoolSize
            minimumIdle = 2
            connectionTimeout = 5000
            idleTimeout = 600000
            maxLifetime = 1800000
            driverClassName = "com.mysql.cj.jdbc.Driver"
        }
        dataSource = HikariDataSource(config)
        initTable()
        info("[Database] MySQL 连接成功")
    }

    fun disconnect() {
        if (Database::dataSource.isInitialized) dataSource.close()
    }

    private fun initTable() {
        dataSource.connection.use { conn ->
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS auction_items (
                    uuid        VARCHAR(36)  PRIMARY KEY,
                    item_name   VARCHAR(255) NOT NULL,
                    amount      INT          NOT NULL DEFAULT 1,
                    price       BIGINT       NOT NULL,
                    seller_uuid VARCHAR(36)  NOT NULL,
                    expire_at   BIGINT       NOT NULL,
                    status      VARCHAR(16)  NOT NULL DEFAULT 'active',
                    version     INT          NOT NULL DEFAULT 0
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """.trimIndent())
        }
    }

    // ── 查询 ──────────────────────────────────────────────

    /** 查询一口价列表（分页，每页 20 条） */
    fun queryAuctionPage(page: Int): List<AuctionItem> {
        val offset = (page - 1) * 20
        val sql = "SELECT * FROM auction_items WHERE status='active' AND expire_at > ? ORDER BY expire_at ASC LIMIT 20 OFFSET ?"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setLong(1, System.currentTimeMillis() / 1000)
                ps.setInt(2, offset)
                val rs = ps.executeQuery()
                val list = mutableListOf<AuctionItem>()
                while (rs.next()) {
                    list += AuctionItem(
                        uuid       = rs.getString("uuid"),
                        name       = rs.getString("item_name"),
                        amount     = rs.getInt("amount"),
                        price      = rs.getLong("price"),
                        sellerUuid = rs.getString("seller_uuid"),
                        expireAt   = rs.getLong("expire_at")
                    )
                }
                list
            }
        }
    }

    /** 查询总条数（用于分页） */
    fun countActiveItems(): Int {
        val sql = "SELECT COUNT(*) FROM auction_items WHERE status='active' AND expire_at > ?"
        return dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setLong(1, System.currentTimeMillis() / 1000)
                val rs = ps.executeQuery()
                if (rs.next()) rs.getInt(1) else 0
            }
        }
    }

    // ── 写操作 ────────────────────────────────────────────

    /** 上架物品，返回是否成功 */
    fun listItem(sellerUuid: String, itemName: String, amount: Int, price: Long, expireHours: Int): Boolean {
        val sql = "INSERT INTO auction_items (uuid, item_name, amount, price, seller_uuid, expire_at) VALUES (?,?,?,?,?,?)"
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, UUID.randomUUID().toString())
                    ps.setString(2, itemName)
                    ps.setInt(3, amount)
                    ps.setLong(4, price)
                    ps.setString(5, sellerUuid)
                    ps.setLong(6, System.currentTimeMillis() / 1000 + expireHours * 3600L)
                    ps.executeUpdate() > 0
                }
            }
        } catch (e: Exception) {
            warning("[Database] 上架失败: ${e.message}")
            false
        }
    }

    /** 购买物品（乐观锁），返回是否成功 */
    fun buyItem(itemUuid: String, buyerUuid: String): Boolean {
        val sql = "UPDATE auction_items SET status='sold', version=version+1 WHERE uuid=? AND status='active' AND version=(SELECT version FROM (SELECT version FROM auction_items WHERE uuid=?) t)"
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, itemUuid)
                    ps.setString(2, itemUuid)
                    ps.executeUpdate() > 0
                }
            }
        } catch (e: Exception) {
            warning("[Database] 购买失败: ${e.message}")
            false
        }
    }

    /** 下架物品（仅卖家可操作），返回是否成功 */
    fun cancelItem(itemUuid: String, sellerUuid: String): Boolean {
        val sql = "UPDATE auction_items SET status='cancelled' WHERE uuid=? AND seller_uuid=? AND status='active'"
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { ps ->
                    ps.setString(1, itemUuid)
                    ps.setString(2, sellerUuid)
                    ps.executeUpdate() > 0
                }
            }
        } catch (e: Exception) {
            warning("[Database] 下架失败: ${e.message}")
            false
        }
    }
}
