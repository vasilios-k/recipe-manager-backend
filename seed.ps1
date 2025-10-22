# seed.ps1  (ASCII-safe)
$ErrorActionPreference = "Stop"

# Basis-URL: lokal oder per ENV ueberschreiben (z.B. https://<dein-backend>.onrender.com)
$base = $env:API_BASE; if (-not $base) { $base = "http://localhost:8080" }

function Post($obj) {
  $json = $obj | ConvertTo-Json -Depth 10
  Invoke-RestMethod -Method POST -Uri ($base + "/recipes") -ContentType "application/json; charset=utf-8" -Body $json
}

$recipes = @(
  @{
    title="Vegane Buddha-Bowl"; description="Bunte Bowl mit Kichererbsen, Quinoa und Avocado."
    prepMinutes=12; cookMinutes=15
    dietTags=@("VEGAN","GLUTEN_FREE","HIGH_FIBER")
    categories=@("vegan","bowl")
    ingredients=@(
      @{name="Quinoa (gekocht)";amount=250;unit="g"},
      @{name="Kichererbsen (Dose, abgetropft)";amount=240;unit="g"},
      @{name="Avocado";amount=1;unit="Stueck"},
      @{name="Spinat frisch";amount=80;unit="g"},
      @{name="Sesam";amount=1;unit="EL"}
    )
    steps=@(
      @{position=1;text="Quinoa kochen."},
      @{position=2;text="Kichererbsen abspuelen, Avocado wuerfeln."},
      @{position=3;text="Alles anrichten, mit Spinat und Sesam toppen."}
    )
  },
  @{
    title="Lachs-Zitronen-Pasta"; description="Schnelle Pasta mit Lachs, Zitrone und Petersilie."
    prepMinutes=8; cookMinutes=12
    dietTags=@("PESCETARIAN","HIGH_PROTEIN")
    categories=@("pasta","schnell")
    ingredients=@(
      @{name="Pasta";amount=250;unit="g"},
      @{name="Lachsfilet";amount=200;unit="g"},
      @{name="Zitrone (Abrieb)";amount=1;unit="Stueck"},
      @{name="Petersilie";amount=2;unit="EL"}
    )
    steps=@(
      @{position=1;text="Pasta kochen."},
      @{position=2;text="Lachs wuerfeln und kurz anbraten."},
      @{position=3;text="Mit Zitronenabrieb und Petersilie vermengen."}
    )
  },
  @{
    title="Low-Carb Haehnchen-Gemuesepfanne"; description="Schnelle Pfanne mit Haehnchen, Paprika und Zucchini."
    prepMinutes=12; cookMinutes=15
    dietTags=@("OMNIVORE","LOW_CARB","HALAL","HIGH_PROTEIN")
    categories=@("pfanne","lowcarb")
    ingredients=@(
      @{name="Haehnchenbrust";amount=300;unit="g"},
      @{name="Paprika";amount=2;unit="Stueck"},
      @{name="Zucchini";amount=1;unit="Stueck"},
      @{name="Olivenoel";amount=1;unit="EL"}
    )
    steps=@(
      @{position=1;text="Gemuese schneiden, Haehnchen wuerfeln."},
      @{position=2;text="In Oel anbraten, wuerzen und servieren."}
    )
  },
  @{
    title="Protein-Pfannkuchen"; description="Schnelle Pfannkuchen mit Quark; glutenfrei moeglich."
    prepMinutes=5; cookMinutes=10
    dietTags=@("VEGETARIAN","GLUTEN_FREE","HIGH_PROTEIN","LOW_SUGAR")
    categories=@("fruehstueck","schnell")
    ingredients=@(
      @{name="Eier";amount=2;unit="Stueck"},
      @{name="Magerquark";amount=150;unit="g"},
      @{name="Glutenfreies Mehl";amount=50;unit="g"},
      @{name="Backpulver";amount=1;unit="TL"}
    )
    steps=@(
      @{position=1;text="Alle Zutaten verruehren."},
      @{position=2;text="In einer Pfanne portionsweise ausbacken."}
    )
  },
  @{
    title="One-Pot Chili (Keto)"; description="Wuerziges Chili ohne Bohnen - keto-freundlich, one-pot."
    prepMinutes=10; cookMinutes=30
    dietTags=@("OMNIVORE","KETO","LOW_CARB","ONE_POT","SPICY")
    categories=@("eintopf","mealprep")
    ingredients=@(
      @{name="Rinderhack";amount=400;unit="g"},
      @{name="Paprika";amount=1;unit="Stueck"},
      @{name="Tomaten (stueckig)";amount=400;unit="g"},
      @{name="Chiliflocken";amount=1;unit="TL"}
    )
    steps=@(
      @{position=1;text="Hack anbraten, Paprika wuerfeln."},
      @{position=2;text="Tomaten und Gewuerze zugeben, koecheln lassen."}
    )
  }
)

$ids = @()
foreach($r in $recipes) {
  $res = Post $r
  $ids += $res.id
  Write-Host ("Created ID: " + $res.id)
}
Write-Host ("Done. IDs: " + ($ids -join ", "))
