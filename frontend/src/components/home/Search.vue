<template>
  <div class="simple-search-bar-container">
    <div class="simple-search-bar">
      <span class="search-icon">
        <svg
          fill="none"
          height="24"
          stroke="currentColor"
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="3"
          viewBox="0 0 36 24"
          width="27"
          xmlns="http://www.w3.org/2000/svg"
        >
          <circle cx="14" cy="15" r="9"></circle>
          <line x1="25" x2="21.65" y1="26" y2="22.65"></line>
        </svg>
      </span>
      <input
        :placeholder="$t('home.search')"
        aria-label="Search"
        class="search-input"
        type="search"
      />
    </div>
  </div>
</template>

<style scoped>
.simple-search-bar-container {
  width: 100%;
  box-sizing: border-box;
  box-shadow: inset 0 2px 6px 0 rgba(0, 0, 0, 0.1);
  border-radius: 50px;
}

.simple-search-bar {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 50px;
  padding: 10px 20px;
  box-sizing: border-box;
  transition:
    box-shadow 0.1s ease-in-out,
    background-color 0.1s ease-in-out;
}

.simple-search-bar:focus-within {
  transition:
    box-shadow 0.1s ease-in-out,
    background-color 0.1s ease-in-out;
  box-shadow: 0 1px 4px 0 rgba(0, 0, 0, 0.1);
  background-color: white;
}

.search-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #555;
  pointer-events: none;
  z-index: 1;
}

.search-input {
  flex-grow: 1;
  width: 100%;
  padding: 8px 10px 8px 35px;
  border: none;
  outline: none;
  background-color: transparent;
  font-size: 1.4rem;
  color: #333;
  border-radius: inherit;
}

.search-input::placeholder {
  color: #999;
}

.search-input::-webkit-search-cancel-button,
.search-input::-webkit-search-decoration {
  -webkit-appearance: none;
  appearance: none;
}
</style>
