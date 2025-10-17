Write-Host "🔧 Fixing PayPal Payment Issues..." -ForegroundColor Green

# Navigate to frontend directory
Set-Location "frontend"

Write-Host "📦 Cleaning node_modules and package-lock.json..." -ForegroundColor Yellow
if (Test-Path "node_modules") {
    Remove-Item -Recurse -Force "node_modules"
}
if (Test-Path "package-lock.json") {
    Remove-Item -Force "package-lock.json"
}

Write-Host "📥 Installing dependencies with React 18..." -ForegroundColor Yellow
npm install

Write-Host "🚀 Starting development server..." -ForegroundColor Green
npm run dev