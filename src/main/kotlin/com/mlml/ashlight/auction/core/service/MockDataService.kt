package com.mlml.ashlight.auction.core.service

/**
 * 模拟数据服务 - 模拟从 Redis 读取的拍卖物品数据
 */
object MockDataService {
    
    // 每页显示数量
    const val PAGE_SIZE = 10
    
    // 模拟 100 个普通拍卖物品
    val mockItems: List<AuctionItem> by lazy {
        val categories = listOf("武器", "防具", "材料", "消耗品", "饰品")
        val itemNames = listOf(
            "§b钻石剑", "§a铁胸甲", "§d下界合金锭", "§e金苹果", "§6附魔书",
            "§c烈焰棒", "§9末影珍珠", "§5附魔金苹果", "§2绿宝石", "§7铁锭",
            "§b锋利 V 钻石剑", "§a保护 IV 铁胸甲", "§d效率 V 钻石镐", "§e力量 V 弓"
        )
        val sellers = listOf("张三", "李四", "王五", "赵六", "Admin", "Player_A", "小明", "小红")
        
        (1..100).map { id ->
            AuctionItem(
                id = id,
                itemName = "${itemNames.random()} #${String.format("%03d", id)}",
                category = categories.random(),
                seller = sellers.random(),
                price = (100..50000).random().toLong(),
                expireAt = System.currentTimeMillis() / 1000 + (3600..86400).random()
            )
        }
    }
    
    // 模拟 100 个竞拍物品
    val mockAuctionItems: List<AuctionBidItem> by lazy {
        val categories = listOf("武器", "防具", "材料", "消耗品", "饰品")
        val rareNames = listOf(
            "§6★ 龙之脊梁 (神话)", "§5☆ 暗影之刃 (传说)", "§b◆ 泰坦之心 (史诗)",
            "§e▲ 智慧法典 (稀有)", "§c● 血腥战锤 (精良)", "§9■ 守护者盾牌 (史诗)",
            "§d★ 凤凰羽翼 (传说)", "§a☆ 自然法杖 (稀有)", "§b◆ 冰霜之刃 (史诗)"
        )
        val sellers = listOf("Admin", "VIP玩家", "土豪", "收藏家", "大佬")
        
        (101..200).map { id ->
            AuctionBidItem(
                id = id,
                itemName = "${rareNames.random()} #${String.format("%03d", id)}",
                category = categories.random(),
                seller = sellers.random(),
                price = (10000..100000).random().toLong(),
                currentBid = (0..50000).random().toLong(),
                expireAt = System.currentTimeMillis() / 1000 + (3600..86400).random()
            )
        }
    }
    
    /**
     * 获取普通拍卖物品分页数据
     */
    fun getItemsPage(page: Int): PageResult<AuctionItem> {
        val totalItems = mockItems.size
        val totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE
        val validPage = page.coerceIn(1, totalPages)
        val startIndex = (validPage - 1) * PAGE_SIZE
        val endIndex = minOf(startIndex + PAGE_SIZE, totalItems)
        
        return PageResult(
            page = validPage,
            totalPages = totalPages,
            total = totalItems,
            items = mockItems.subList(startIndex, endIndex)
        )
    }
    
    /**
     * 获取竞拍物品分页数据
     */
    fun getAuctionItemsPage(page: Int): PageResult<AuctionBidItem> {
        val totalItems = mockAuctionItems.size
        val totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE
        val validPage = page.coerceIn(1, totalPages)
        val startIndex = (validPage - 1) * PAGE_SIZE
        val endIndex = minOf(startIndex + PAGE_SIZE, totalItems)
        
        return PageResult(
            page = validPage,
            totalPages = totalPages,
            total = totalItems,
            items = mockAuctionItems.subList(startIndex, endIndex)
        )
    }
}

/**
 * 普通拍卖物品
 */
data class AuctionItem(
    val id: Int,
    val itemName: String,
    val category: String,
    val seller: String,
    val price: Long,
    val expireAt: Long
)

/**
 * 竞拍物品
 */
data class AuctionBidItem(
    val id: Int,
    val itemName: String,
    val category: String,
    val seller: String,
    val price: Long,
    val currentBid: Long,
    val expireAt: Long
)

/**
 * 分页结果
 */
data class PageResult<T>(
    val page: Int,
    val totalPages: Int,
    val total: Int,
    val items: List<T>
)
