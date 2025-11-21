# Fix package declarations and imports for multi-module structure

Write-Host "Fixing package names in core module..."
Get-ChildItem -Path "C:\Users\a50048601\pfe\demo\core\src" -Filter "*.java" -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package com\.example\.demo\.orchestrator', 'package com.example.demo.core'
    $content = $content -replace 'import com\.example\.demo\.orchestrator', 'import com.example.demo.core'
    $content = $content -replace 'import com\.example\.demo\.llm_adapter', 'import com.example.demo.ai'
    $content = $content -replace 'import com\.example\.demo\.Runner', 'import com.example.demo.runner'
    Set-Content $_.FullName -Value $content -NoNewline
}

Write-Host "Fixing package names in ai-generation-service module..."
Get-ChildItem -Path "C:\Users\a50048601\pfe\demo\ai-generation-service\src" -Filter "*.java" -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package com\.example\.demo\.llm_adapter', 'package com.example.demo.ai'
    $content = $content -replace 'import com\.example\.demo\.llm_adapter', 'import com.example.demo.ai'
    $content = $content -replace 'import com\.example\.demo\.orchestrator', 'import com.example.demo.core'
    Set-Content $_.FullName -Value $content -NoNewline
}

Write-Host "Fixing package names in test-execution-service module..."
Get-ChildItem -Path "C:\Users\a50048601\pfe\demo\test-execution-service\src" -Filter "*.java" -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'package com\.example\.demo\.Runner', 'package com.example.demo.runner'
    $content = $content -replace 'import com\.example\.demo\.Runner', 'import com.example.demo.runner'
    $content = $content -replace 'import com\.example\.demo\.orchestrator', 'import com.example.demo.core'
    Set-Content $_.FullName -Value $content -NoNewline
}

Write-Host "Package name fixes completed!"
