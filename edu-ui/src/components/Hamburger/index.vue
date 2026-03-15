<template>
  <div class="hamburger-wrap" :class="{'is-active': isActive}" @click="toggleClick">
    <span class="bar"></span>
    <span class="bar"></span>
    <span class="bar"></span>
  </div>
</template>

<script>
export default {
  name: 'Hamburger',
  props: {
    isActive: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    toggleClick() {
      this.$emit('toggleClick')
    }
  }
}
</script>

<style scoped>
.hamburger-wrap {
  display: inline-flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 36px;
  height: 36px;
  cursor: pointer;
  padding: 6px;
  border-radius: 8px;
  transition: background-color 0.25s ease;
  margin: 0 8px;
}

.hamburger-wrap:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

.bar {
  display: block;
  width: 18px;
  height: 2px;
  background-color: #4b5563;
  border-radius: 2px;
  transition: all 0.35s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.bar + .bar {
  margin-top: 4px;
}

/* 展开状态 → X 形变 */
.is-active .bar:nth-child(1) {
  transform: translateY(6px) rotate(45deg);
}
.is-active .bar:nth-child(2) {
  opacity: 0;
  transform: scaleX(0);
}
.is-active .bar:nth-child(3) {
  transform: translateY(-6px) rotate(-45deg);
}
</style>
