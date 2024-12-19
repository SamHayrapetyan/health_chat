package com.dinno.health_chat.api

object HealthChatManagerHolder {

    var manager: HealthChatManager? = null
        private set

    fun initManager(manager: HealthChatManager) {
        this.manager = manager
    }

    fun clearHolder() {
        manager = null
    }
}