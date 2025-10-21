# Recipe API Smoke & Negative Tests (ASCII only, PS 5.1)
$Base = "http://localhost:8080"

function Invoke-Json {
    param(
        [ValidateSet('GET','POST','PUT','DELETE')][string]$Method,
        [string]$Path,
        $Body
    )
    $uri = "$Base$Path"
    $json = $null
    if ($PSBoundParameters.ContainsKey('Body')) {
        $json = $Body | ConvertTo-Json -Depth 10 -Compress
    }
    try {
        $resp = Invoke-WebRequest -Uri $uri -Method $Method -ContentType 'application/json; charset=utf-8' -Body $json -UseBasicParsing
        $status = [int]$resp.StatusCode
        $text = $resp.Content
    } catch [System.Net.WebException] {
        $res = $_.Exception.Response
        if (-not $res) { throw }
        $status = [int]$res.StatusCode
        $sr = New-Object IO.StreamReader($res.GetResponseStream())
        $text = $sr.ReadToEnd()
        $sr.Close()
    }
    $body = $null
    if ($text -and ($text.TrimStart().StartsWith('{') -or $text.TrimStart().StartsWith('['))) {
        try { $body = $text | ConvertFrom-Json } catch { }
    }
    return @{ Status=$status; Text=$text; Body=$body }
}

function SafeCount($obj) {
    if ($null -eq $obj) { return 0 }
    try { return $obj.Count } catch { return 0 }
}

function Print-Result {
    param([string]$Step,[int]$Expected,[hashtable]$Res,[string]$Extra="")
    if ($Res.Status -eq $Expected) {
        Write-Host ("OK  {0} -> {1}" -f $Step, $Res.Status) -ForegroundColor Green
    } else {
        Write-Host ("ERR {0} -> {1} (expected {2})" -f $Step, $Res.Status, $Expected) -ForegroundColor Red
        if ($Res.Text) { Write-Host $Res.Text -ForegroundColor DarkYellow }
    }
    if ($Extra) { Write-Host ("    {0}" -f $Extra) -ForegroundColor DarkCyan }
}

# 0) Ping
$R0 = Invoke-Json GET "/recipes"
$cnt0 = 0; if ($R0.Body) { $cnt0 = SafeCount $R0.Body }
Print-Result "GET /recipes (list)" 200 $R0 ("count=" + $cnt0)

# 1) Create
$CreateBody = @{
  title="Vegane Bowl"; description="Bunte Bowl"; prepMinutes=10; cookMinutes=5
  dietTags=@("VEGAN","GLUTEN_FREE"); categories=@("vegan","bowl")
  ingredients=@(@{name="Kichererbsen";amount=240;unit="g"}, @{name="Spinat";amount=100;unit="g"})
  steps=@(@{position=1;text="Schneiden"}, @{position=2;text="Anrichten"})
}
$R1 = Invoke-Json POST "/recipes" $CreateBody
Print-Result "POST /recipes (create)" 201 $R1
$id = $null
if ($R1.Body -and $R1.Body.PSObject.Properties.Name -contains "id") { $id = $R1.Body.id }
if (-not $id) { throw "No ID from create - abort." }

# 2) Read
$R2 = Invoke-Json GET "/recipes/$id"
$t2 = ""; if ($R2.Body) { $t2 = ("title={0}, total={1}" -f $R2.Body.title, $R2.Body.totalMinutes) }
Print-Result "GET /recipes/$id" 200 $R2 $t2

# 3) PUT Base (only base fields)
$UpdateBase = @{
  title="Vegane Bowl (ueberarbeitet)"; description="Bunte Bowl mit Spinat"
  prepMinutes=12; cookMinutes=6
  dietTags=@("VEGAN","GLUTEN_FREE"); categories=@("vegan","bowl","quick")
}
$R3 = Invoke-Json PUT "/recipes/$id" $UpdateBase
Print-Result "PUT /recipes/$id (base)" 204 $R3

# 4) Replace Ingredients
$NewIngredients = @(
  @{ name="Kichererbsen"; amount=200; unit="g" },
  @{ name="Gurke"; amount=120; unit="g" }
)
$R4 = Invoke-Json PUT "/recipes/$id/ingredients" $NewIngredients
Print-Result "PUT /recipes/$id/ingredients" 204 $R4

# 5) Replace Steps (avoid & by using 'and')
$NewSteps = @(
  @{ position=1; text="Rinse and cut" },
  @{ position=2; text="Arrange and serve" }
)
$R5 = Invoke-Json PUT "/recipes/$id/steps" $NewSteps
Print-Result "PUT /recipes/$id/steps" 204 $R5

# 6) Verify
$R6 = Invoke-Json GET "/recipes/$id"
$extra6 = ""
if ($R6.Body) {
  $ingCount = SafeCount $R6.Body.ingredients
  $stepCount = SafeCount $R6.Body.steps
  $extra6 = ("title={0}; ingredients={1}; steps={2}" -f $R6.Body.title, $ingCount, $stepCount)
}
Print-Result "GET /recipes/$id (verify)" 200 $R6 $extra6

# 7) Negative: two baselines -> 400
$BadBaseline = @{
  title="X"; description=""; prepMinutes=0; cookMinutes=0
  dietTags=@("VEGAN","VEGETARIAN"); categories=@("x")
  ingredients=@(@{name="A";amount=1;unit="g"}); steps=@(@{position=1;text="x"})
}
$R7 = Invoke-Json POST "/recipes" $BadBaseline
Print-Result "POST /recipes (two baselines)" 400 $R7

# 8) Negative: missing title -> 400
$BadValidation = @{
  description=""; prepMinutes=0; cookMinutes=0
  dietTags=@("VEGAN"); categories=@("x")
  ingredients=@(@{name="A";amount=1;unit="g"}); steps=@(@{position=1;text="x"})
}
$R8 = Invoke-Json POST "/recipes" $BadValidation
Print-Result "POST /recipes (missing title)" 400 $R8

# 9) Delete
$R9 = Invoke-Json DELETE "/recipes/$id"
Print-Result "DELETE /recipes/$id" 204 $R9

# 10) Verify 404
$R10 = Invoke-Json GET "/recipes/$id"
Print-Result "GET /recipes/$id (after delete)" 404 $R10

Write-Host ""
Write-Host "Done. If any step is red, see the yellow response above." -ForegroundColor Cyan
