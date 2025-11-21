# Fix specific import issues in ai-generation-service

Write-Host "Fixing DTO imports in ai-generation-service..."
Get-ChildItem -Path "C:\Users\a50048601\pfe\demo\ai-generation-service\src" -Filter "*.java" -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw

    # Fix AssertionData import
    $content = $content -replace 'import com\.example\.demo\.ai\.dto\.AssertionData', 'import com.example.demo.ai.dto.assertion.AssertionData'

    # Fix RestRequestData import
    $content = $content -replace 'import com\.example\.demo\.ai\.dto\.RestRequestData', 'import com.example.demo.ai.dto.request.RestRequestData'

    # Fix SoapRequestData import
    $content = $content -replace 'import com\.example\.demo\.ai\.dto\.SoapRequestData', 'import com.example.demo.ai.dto.request.SoapRequestData'

    # Fix CreateRestApiTestRequest import
    $content = $content -replace 'import com\.example\.demo\.ai\.dto\.CreateRestApiTestRequest', 'import com.example.demo.ai.dto.spec2suite.test.CreateRestApiTestRequest'

    # Fix CreateSoapApiTestRequest import
    $content = $content -replace 'import com\.example\.demo\.ai\.dto\.CreateSoapApiTestRequest', 'import com.example.demo.ai.dto.spec2suite.test.CreateSoapApiTestRequest'

    Set-Content $_.FullName -Value $content -NoNewline
}

Write-Host "Deleting old AiConfig.java..."
Remove-Item "C:\Users\a50048601\pfe\demo\ai-generation-service\src\main\java\com\example\demo\ai\AiConfig.java" -ErrorAction SilentlyContinue

Write-Host "Deleting old Generator files..."
Remove-Item "C:\Users\a50048601\pfe\demo\ai-generation-service\src\main\java\com\example\demo\ai\old" -Recurse -ErrorAction SilentlyContinue

Write-Host "Import fixes completed!"
