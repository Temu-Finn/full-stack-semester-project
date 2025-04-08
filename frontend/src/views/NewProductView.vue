<template>
  <div class="new-product-view">
    <h1>{{ $t('newProduct.title') }}</h1>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="name">{{ $t('newProduct.productTitle') }}</label>
        <input id="name" v-model="product.name" required type="text" />
      </div>

      <div class="form-group">
        <label for="description">{{ $t('newProduct.description') }}</label>
        <textarea id="description" v-model="product.description" auto-resize required></textarea>
      </div>

      <div class="form-group">
        <label for="price">{{ $t('newProduct.price') }}</label>
        <input id="price" v-model="product.price" required type="number" />
      </div>

      <div class="form-group">
        <label for="location">{{ $t('newProduct.location') }}</label>
        <input id="location" v-model="product.location" required type="text" />
      </div>

      <div class="form-group">
        <label for="image">{{ $t('newProduct.image') }}</label>
        <input id="image" accept="image/*" type="file" @change="handleImageUpload" />
        <div v-if="product.imageUrl" class="image-preview-container">
          <img :src="product.imageUrl" alt="Product Preview" class="image-preview" />
        </div>
      </div>

      <div class="form-group toggle-group">
        <input id="vipps" v-model="product.acceptVipps" checked type="checkbox" />
        <label class="toggle-label" for="vipps">{{ $t('newProduct.vipps') }}</label>
      </div>

      <button type="submit">Create Product</button>
    </form>
  </div>
</template>

<script>
export default {
  name: 'NewProductView',
  data() {
    return {
      product: {
        name: '',
        description: '',
        price: null,
        location: '',
        image: null,
        imageUrl: '',
        acceptVipps: false,
      },
    }
  },
  methods: {
    handleImageUpload(event) {
      const file = event.target.files[0]
      if (file) {
        this.product.image = file
        this.product.imageUrl = URL.createObjectURL(file)
      }
    },
    handleSubmit() {
      const formData = new FormData()
      formData.append('name', this.product.name)
      formData.append('description', this.product.description)
      formData.append('price', this.product.price)
      formData.append('location', this.product.location)
      formData.append('acceptVipps', this.product.acceptVipps)
      if (this.product.image) {
        formData.append('image', this.product.image)
      }

      for (let [key, value] of formData.entries()) {
        console.log(`${key}:`, value)
      }

      // TODO: Replace with your API call (e.g., using fetch or axios)
      alert('Product created! (See console for submitted data)')
    },
  },
}
</script>

<style scoped>
.new-product-view {
  max-width: 600px;
  margin: 40px auto;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

input[type='text'],
input[type='number'],
textarea,
input[type='file'] {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid lightgray;
  border-radius: 6px;
  box-shadow:
    inset 0 1px 4px rgba(0, 0, 0, 0.075),
    0 0 0 rgba(0, 0, 0, 0);
  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease;
}

input[type='text']:focus,
input[type='number']:focus,
textarea:focus,
input[type='file']:focus {
  outline: none;
  border-color: gray;
  box-shadow:
    inset 0 0 0 rgba(0, 0, 0, 0.075),
    0 2px 8px rgba(0, 0, 0, 0.2);
}

.image-preview-container {
  margin-top: 10px;
}

.image-preview {
  width: 100%;
  border: 1px solid black;
  border-radius: 6px;
}

.toggle-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toggle-group input[type='checkbox'] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}
.toggle-group label {
  margin: 4px 0;
}

button {
  width: 100%;
  padding: 12px;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
  margin-top: 10px;
}

textarea {
  resize: vertical;
}

button:hover {
  background-color: gray;
}
</style>
