module.exports = {
  root: true,
  env: {
    node: true,
  },
  'extends': [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/typescript/recommended'
  ],
  parserOptions: {
    ecmaVersion: 2020
  },
  rules: {
    'no-console' : process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger' : process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'vue/multi-word-component-names' : 'off',
    'vue/no-unused-vars' : 'off',
    '@typescript-eslint/ban-ts-comment' : 'off',
    '@typescript-eslint/no-empty-function' : 'off',
    'vue/no-unused-components' : 'off',
    '@typescript-eslint/no-extra-semi' : 'off',
    '@typescript-eslint/no-var-requires': 0
  }
}
