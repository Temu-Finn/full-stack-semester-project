<template>
  <div class="container">
    <aside v-if="!isLoading" class="sidebar">
      <button v-if="isSidebarOpen" class="close-sidebar">&times;</button>
      <div
        v-for="conversation in conversationCardsResponse.value.conversations || []"
        :key="conversation.id"
        class="contact"
        @click="handleContactClick(conversation.id)"
      >
        <div class="avatar">lol</div>
        <div class="details">
          <div class="name">{{ conversation.name }}</div>
          <div class="last-text">{{ conversation.lastMessage }}</div>
        </div>
      </div>
    </aside>

    <div :class="isSidebarOpen ? 'overlay' : 'overlay hidden'" @click="closeSidebar"></div>

    <!--main v-if="currentConversation" class="chat-window">
      <button class="open-sidebar-btn" @click="openSidebar">
        {{ $t('chat.allConversations') }}
      </button>
      <div class="chat-header">
        <div class="chat-avatar"></div>
        <div class="chat-info">
          <div class="chat-name name">{{ currentConversation.name }}</div>
          <div class="chat-status">
            {{ currentConversation.online ? $t('chat.online') : $t('chat.offline') }}
          </div>
        </div>
      </div>

      <div class="chat-body">
        <div
          v-for="(msg, index) in currentConversation.messages"
          :key="index"
          :class="msg.type"
          class="message"
        >
          {{ msg.content }}
        </div>
      </div>

      <div class="chat-footer">
        <div class="message-input-container">
          <input
            v-model="newMessage"
            :placeholder="$t('chat.writeMessage')"
            type="text"
            @keyup.enter="sendMessage"
          />
          <button class="send-btn" @click="sendMessage">{{ $t('chat.send') }}</button>
        </div>
      </div>
    </main-->
  </div>
</template>

<script lang="ts" setup>
import {getConversations, type ConversationCardsResponse} from "@/service/conversationService.js";
import {onMounted, ref} from "vue";

const isLoading = ref(true)
const conversationCardsResponse = ref<ConversationCardsResponse>()

onMounted(() => {
  fetchConversations()
})

async function fetchConversations() {
  isLoading.value = true
  try {
    conversationCardsResponse.value = await getConversations();
    console.log('Conversations fetched:', conversationCardsResponse.value.conversations[0].id)
  } catch(error) {
    console.error('Error fetching conversations:', error)
    conversationCardsResponse.value = null;
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.container {
  display: flex;
  height: calc(100vh - 60px);
  width: 100%;
}

.sidebar {
  width: 30%;
  padding-top: 15px;
  border-right: 1px solid #ddd;
  overflow-y: auto;
  background-color: #fff;
}

.contact {
  border-radius: 8px 0 0 8px;
  display: flex;
  align-items: center;
  padding: 10px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: background-color 0.1s ease;
}

.contact:hover {
  background-color: #f5f5f5;
}

.avatar {
  width: 40px;
  height: 40px;
  background-color: #ddd;
  border-radius: 6px;
}

.details {
  margin-left: 10px;
}

.name {
  font-weight: bold;
  color: #444;
  line-height: 0.9rem;
}

.last-text {
  color: #888;
  font-size: small;
}

.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  width: 70%;
  justify-content: space-between;
}

.chat-header {
  display: flex;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #ddd;
}

.chat-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #ddd;
}

.chat-info {
  margin-left: 15px;
}

.chat-status {
  color: #888;
}

.chat-body {
  flex-grow: 1;
  margin-top: 20px;
  display: flex;
  flex-direction: column-reverse;
  overflow-y: auto;
}

.message {
  padding: 8px 12px;
  border-radius: 10px;
  background-color: #ccc;
  margin-bottom: 10px;
  text-wrap: wrap;
  width: max-content;
  overflow: hidden;
  min-height: fit-content;
}

.received {
  max-width: 60%;
  background-color: #d2efef;
}

.sent {
  max-width: 80%;
  margin-left: auto;
  background-color: #fde7d3;
}

.chat-footer {
  margin-top: 15px;
}

.message-input-container {
  display: flex;
  align-items: center;
}

.message-input-container input {
  flex: 1;
  padding: 14px;
  border-radius: 50px;
  border: 1px solid #ddd;
  outline: none;
  font-size: medium;
}

.send-btn {
  margin-left: 10px;
  padding: 14px 20px;
  border: none;
  border-radius: 50px;
  background-color: #007bff;
  color: #fff;
  cursor: pointer;
  font-size: medium;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  transition: background-color 0.2s ease;
  background-color: rgba(0, 0, 0, 0.5);
  pointer-events: all;
}

.hidden {
  background-color: rgba(0, 0, 0, 0);
  pointer-events: none;
}

.open-sidebar-btn {
  display: none;
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
    height: calc(100vh - 60px);
  }

  .sidebar {
    position: fixed;
    top: 0;
    left: -100%;
    height: 100%;
    width: 75%;
    border-right: 1px solid #ddd;
    z-index: 1001;
    transition: left 0.3s ease;
    padding-top: 15px;
  }
  .sidebar.mobile-open {
    left: 0;
  }

  .close-sidebar {
    background: none;
    border: none;
    font-size: 2rem;
    cursor: pointer;
    text-align: end;
    padding: 0 1rem;
    width: 100%;
  }

  .chat-header {
    padding: 15px 0;
    border-top: 1px solid #ddd;
  }

  .chat-window {
    width: 100%;
    padding: 15px;
    max-height: 100%;
  }

  .open-sidebar-btn {
    display: block;
    padding: 10px 15px;
    margin-bottom: 15px;
    background-color: #d2efef;
    border: none;
    color: rgba(0, 0, 0, 0.7);
    border-radius: 4px;
    cursor: pointer;
    font-size: 1rem;
  }
}
</style>
