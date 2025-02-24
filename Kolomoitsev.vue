<script>
/* ---------- IMPORTS ---------- */
import BlockNode from "./BlockNode.vue";

/* ---------- CONSTANTS ---------- */
const inputColor = "bg-blue-400"
const outputColor = "bg-green-500"

/* ---------- VUE OBJECTS ---------- */
export default {
  components: {BlockNode},
  props: {
    text: {
      type: String,
      required: false,
    },
    type: {
      type: String,
      required: true,
    },
  },

  data() {
    return {
      nodeSize: 5,
      nodeBorderSize: 3
    }
  },

  computed: {
    isReversed() {
      return this.type.toLocaleLowerCase().includes("out")
    },
    getColor() {
      if (this.isReversed) {
        return outputColor
      }
      else {
        return inputColor
      }
    },
  }
}
</script>

<template>
  <!-- Контейнер с динамическим порядком -->
  <div :class="{ 'flex-row-reverse': isReversed }" class="flex items-center justify-center gap-2">
    <!-- Узел -->
    <BlockNode :main-color="getColor" :size="nodeSize" :border-size="nodeBorderSize"/>
    <!-- Текст узла -->
    <span class="text-lg font-semibold">{{ this.text }}</span>
  </div>
</template>
