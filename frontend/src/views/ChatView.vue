<template>
  <div class="container">
    <aside :class="{ 'mobile-open': isSidebarOpen }" class="sidebar">
      <div
        v-for="contact in contacts"
        :key="contact.id"
        class="contact"
        @click="handleContactClick(contact.id)"
      >
        <div class="avatar"></div>
        <div class="details">
          <div class="name">{{ contact.name }}</div>
          <div class="last-text">{{ contact.lastMessage }}</div>
        </div>
      </div>
    </aside>

    <div :class="isSidebarOpen ? 'overlay' : 'overlay hidden'" @click="closeSidebar"></div>

    <main v-if="currentConversation" class="chat-window">
      <button class="open-sidebar-btn" @click="openSidebar">All conversations</button>
      <div class="chat-header">
        <div class="chat-avatar"></div>
        <div class="chat-info">
          <div class="chat-name name">{{ currentConversation.name }}</div>
          <div class="chat-status">{{ currentConversation.online ? 'Online' : 'Offline' }}</div>
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
            placeholder="Write a message..."
            type="text"
            @keyup.enter="sendMessage"
          />
          <button class="send-btn" @click="sendMessage">Send</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  data() {
    return {
      contacts: [
        { id: 1, name: 'Alice', lastMessage: 'Hey, how are you?' },
        { id: 2, name: 'Bob', lastMessage: 'Are we still on for tomorrow?' },
        { id: 3, name: 'Charlie', lastMessage: 'Check out this link!' },
        { id: 4, name: 'Diana', lastMessage: 'Let me know your thoughts.' },
        { id: 5, name: 'Eve', lastMessage: 'Thanks for the update!' },
        // ... potentially many more contacts
      ],
      conversations: {
        1: {
          name: 'Alice',
          online: true,
          messages: [{ type: 'sent', content: 'Hey, how are you?' }],
        },
        2: {
          name: 'Bob',
          online: false,
          messages: [{ type: 'received', content: 'Are we still on for tomorrow?' }],
        },
        3: {
          name: 'Charlie',
          online: true,
          messages: [
            { type: 'received', content: 'Check out this link!' },
            { type: 'sent', content: 'I will!' },
          ],
        },
        4: {
          name: 'Diana',
          online: true,
          messages: [{ type: 'received', content: 'Let me know your thoughts.' }],
        },
        5: {
          name: 'Eve',
          online: false,
          messages: [{ type: 'sent', content: 'Thanks for the update!' }],
        },
      },
      newMessage: '',
      isSidebarOpen: false,
    }
  },
  computed: {
    currentConversation() {
      return this.conversations[this.$route.params.id]
    },
  },
  methods: {
    handleContactClick(id) {
      this.selectConversation(id)
      this.closeSidebar()
    },
    selectConversation(id) {
      this.$router.push({ name: 'Chat', params: { id } })
    },
    sendMessage() {
      if (this.newMessage.trim() !== '') {
        this.currentConversation.messages.push({
          type: 'sent',
          content: this.newMessage,
        })
        this.newMessage = ''
      }
    },
    openSidebar() {
      this.isSidebarOpen = true
    },
    closeSidebar() {
      this.isSidebarOpen = false
    },
  },
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
  overflow-y: auto;
}

.message {
  padding: 8px 12px;
  border-radius: 10px;
  background-color: #ccc;
  margin-bottom: 10px;
}

.received {
  max-width: 60%;
  width: max-content;
  background-color: #d2efef;
}

.sent {
  max-width: 80%;
  width: max-content;
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

/* Sidebar overlay styles */
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
    width: 90%;
    border-right: 1px solid #ddd;
    z-index: 1001;
    transition: left 0.3s ease;
    padding-top: 15px;
  }
  .sidebar.mobile-open {
    left: 0;
  }

  .chat-header {
    padding: 15px 0;
    border-top: 1px solid #ddd;
  }

  .chat-window {
    width: 100%;
    padding: 15px;
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
