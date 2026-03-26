package com.mlml.ashlight.auction.core.network

/**
 * 消息处理器注册表
 * 用于管理和分发消息处理器
 */
object MessageHandlerRegistry {
    
    private val handlers = mutableMapOf<String, MessageHandler>()
    
    /**
     * 注册消息处理器
     */
    fun register(handler: MessageHandler) {
        handlers[handler.key] = handler
    }
    
    /**
     * 批量注册消息处理器
     */
    fun registerAll(vararg handlers: MessageHandler) {
        handlers.forEach { register(it) }
    }
    
    /**
     * 获取处理器
     */
    fun getHandler(key: String): MessageHandler? = handlers[key]
    
    /**
     * 检查是否存在处理器
     */
    fun hasHandler(key: String): Boolean = handlers.containsKey(key)
    
    /**
     * 清空所有处理器
     */
    fun clear() {
        handlers.clear()
    }
}
