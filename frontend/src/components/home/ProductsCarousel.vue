<script setup>
import { ref } from 'vue'
import Carousel from 'primevue/carousel'

const products = ref([
  {
    id: '1000',
    code: 'f230fh0g3',
    name: 'Bamboo Watch',
    description: 'Product Description',
    image: 'bamboo-watch.jpg',
    price: 65,
    category: 'Accessories',
    quantity: 24,
    inventoryStatus: 'INSTOCK',
    rating: 5,
  },
  {
    id: '1001',
    code: 'nvklal433',
    name: 'Black Watch',
    description: 'Product Description',
    image: 'black-watch.jpg',
    price: 72,
    category: 'Accessories',
    quantity: 61,
    inventoryStatus: 'INSTOCK',
    rating: 4,
  },
  {
    id: '1002',
    code: 'zz21cz3c1',
    name: 'Blue Band',
    description: 'Product Description',
    image: 'blue-band.jpg',
    price: 79,
    category: 'Fitness',
    quantity: 2,
    inventoryStatus: 'LOWSTOCK',
    rating: 3,
  },
  {
    id: '1003',
    code: '244wgerg2',
    name: 'Blue T-Shirt',
    description: 'Product Description',
    image: 'blue-t-shirt.jpg',
    price: 29,
    category: 'Clothing',
    quantity: 25,
    inventoryStatus: 'INSTOCK',
    rating: 5,
  },
  {
    id: '1004',
    code: 'h456wer53',
    name: 'Bracelet',
    description: 'Product Description',
    image: 'bracelet.jpg',
    price: 15,
    category: 'Accessories',
    quantity: 73,
    inventoryStatus: 'INSTOCK',
    rating: 4,
  },
  {
    id: '1005',
    code: 'av2231fwg',
    name: 'Brown Purse',
    description: 'Product Description',
    image: 'brown-purse.jpg',
    price: 120,
    category: 'Accessories',
    quantity: 0,
    inventoryStatus: 'OUTOFSTOCK',
    rating: 4,
  },
  {
    id: '1006',
    code: 'bib36pfvm',
    name: 'Chakra Bracelet',
    description: 'Product Description',
    image: 'chakra-bracelet.jpg',
    price: 32,
    category: 'Accessories',
    quantity: 5,
    inventoryStatus: 'LOWSTOCK',
    rating: 3,
  },
  {
    id: '1007',
    code: 'mbvjkgip5',
    name: 'Galaxy Earrings',
    description: 'Product Description',
    image: 'galaxy-earrings.jpg',
    price: 34,
    category: 'Accessories',
    quantity: 23,
    inventoryStatus: 'INSTOCK',
    rating: 5,
  },
  {
    id: '1008',
    code: 'vbb124btr',
    name: 'Game Controller',
    description: 'Product Description',
    image: 'game-controller.jpg',
    price: 99,
    category: 'Electronics',
    quantity: 2,
    inventoryStatus: 'LOWSTOCK',
    rating: 4,
  },
])

const responsiveOptions = ref([
  {
    breakpoint: '1400px',
    numVisible: 3,
    numScroll: 3,
  },
  {
    breakpoint: '960px',
    numVisible: 2,
    numScroll: 2,
  },
  {
    breakpoint: '600px',
    numVisible: 1,
    numScroll: 1,
  },
])
</script>

<template>
  <Carousel
    :numScroll="3"
    :numVisible="3"
    :responsiveOptions="responsiveOptions"
    :value="products"
    class="carousel"
  >
    <template #item="slotProps">
      <div class="product-item">
        <div class="product-image-section">
          <div class="product-image-container">
            <img
              :alt="slotProps.data.name"
              :src="'https://primefaces.org/cdn/primevue/images/product/' + slotProps.data.image"
              class="product-image"
            />
          </div>
        </div>
        <div class="product-name">{{ slotProps.data.name }}</div>
        <div class="product-details">
          <div class="product-price">${{ slotProps.data.price }}</div>
          <span>
            <Button icon="pi pi-heart" outlined severity="secondary" />
            <Button class="cart-button" icon="pi pi-shopping-cart" />
          </span>
        </div>
      </div>
    </template>
  </Carousel>
</template>

<style scoped>
.carousel {
  width: 100%;
}
.product-item {
  border-width: 1px;
  border-style: solid;
  border-color: #dedede;
  border-radius: 6px;
  margin: 0.5rem;
  padding: 0.75rem;
}

.product-image-section {
  margin-bottom: 0.75rem;
}

.product-image-container {
  position: relative;
  margin-left: auto;
  margin-right: auto;
}

.product-image {
  width: 100%;
  border-radius: 6px;
  display: block;
  max-width: 100%;
}

.product-name {
  margin-bottom: 0.75rem;
  font-weight: 500;
}

.product-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  margin-top: 0;
  font-weight: 600;
  font-size: 1.25rem;
  line-height: 1.75rem;
}

.cart-button {
  margin-left: 0.5rem;
}
</style>
