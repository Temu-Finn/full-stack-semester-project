<template>
  <div class="container">
    <aside class="sidebar">
      <div
        v-for="contact in contacts"
        :key="contact.id"
        class="contact"
        @click="selectConversation(contact.id)"
      >
        <div class="avatar"></div>
        <div class="details">
          <div class="name">{{ contact.name }}</div>
          <div class="last-text">{{ contact.lastMessage }}</div>
        </div>
      </div>
    </aside>

    <main v-if="currentConversation" class="chat-window">
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
        <input
          v-model="newMessage"
          placeholder="Write a message..."
          type="text"
          @keyup.enter="sendMessage"
        />
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
    }
  },
  computed: {
    currentConversation() {
      return this.conversations[this.$route.params.id]
    },
  },
  methods: {
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
  },
}
</script>

<style scoped>
.container {
  display: flex;
  height: calc(100vh - 60px);
  width: 100%;
  font-family: Arial, sans-serif;
}

.sidebar {
  width: 30%;
  padding-top: 15px;
  border-right: 1px solid #ddd;
  overflow-y: auto;
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
  vertical-align: center;
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
  width: 100%;
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

.chat-status {
  color: #888;
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

.chat-body {
  flex-grow: 1;
  margin-top: 20px;
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

.chat-footer input {
  width: 100%;
  padding: 14px;
  border-radius: 50px;
  border: 1px solid #ddd;
  outline: none;
  font-size: medium;
}
</style>
