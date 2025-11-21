# Fix Context DTOs in shared-contracts and update imports

Write-Host "Updating package declarations in shared-contracts Context DTOs..."
Get-ChildItem -Path "C:\Users\a50048601\pfe\demo\shared-contracts\src\main\java\com\example\demo\shared\context" -Filter "*Context.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package com\.example\.demo\.core\.dto', 'package com.example.demo.shared.context'
    $content = $content -replace 'import com\.example\.demo\.ai\.dto', 'import com.example.demo.ai.dto'
    Set-Content $_.FullName -Value $content -NoNewline
}

Write-Host "Updating imports in core module to use shared.context..."
Get-ChildItem -Path "C:\Users\a50048601\pfe\demo\core\src" -Filter "*.java" -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'import com\.example\.demo\.core\.dto\.([A-Z]\w+Context)', 'import com.example.demo.shared.context.$1'
    Set-Content $_.FullName -Value $content -NoNewline
}

Write-Host "Updating imports in ai-generation-service to use shared.context..."
Get-ChildItem -Path "C:\Users\a50048601\pfe\demo\ai-generation-service\src" -Filter "*.java" -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'import com\.example\.demo\.core\.dto\.([A-Z]\w+Context)', 'import com.example.demo.shared.context.$1'
    Set-Content $_.FullName -Value $content -NoNewline
}

Write-Host "Deleting old Context files from core/dto..."
Remove-Item "C:\Users\a50048601\pfe\demo\core\src\main\java\com\example\demo\core\dto\*Context.java" -Force -ErrorAction SilentlyContinue

Write-Host "Shared context fixes completed!"
