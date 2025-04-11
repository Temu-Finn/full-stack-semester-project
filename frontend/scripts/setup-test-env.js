// This script ensures test results directories exist before tests run
import fs from 'node:fs'
import path from 'node:path'

const directories = [
  path.resolve(process.cwd(), 'test-results'),
  path.resolve(process.cwd(), 'coverage'),
]

directories.forEach((dir) => {
  if (!fs.existsSync(dir)) {
    console.log(`Creating directory: ${dir}`)
    fs.mkdirSync(dir, { recursive: true })
  } else {
    console.log(`Directory already exists: ${dir}`)
  }
})

console.log('Test environment setup complete.')
