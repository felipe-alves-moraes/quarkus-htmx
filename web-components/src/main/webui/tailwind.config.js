/** @type {import('tailwindcss').Config} */
export default {
  content: [
      '../resources/templates/**/*.html'
  ],
  theme: {
    extend: {},
  },
  plugins: [
    require('@tailwindcss/typography')
  ],
}

