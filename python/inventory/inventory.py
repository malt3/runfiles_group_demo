import json

import bottle
import humanize
import tabulate

PRODUCTS = [
    {"id": 1, "name": "Widget", "stock": 1200},
    {"id": 2, "name": "Gadget", "stock": 45},
    {"id": 3, "name": "Gizmo", "stock": 1500000},
]


@bottle.route("/products")
def products():
    bottle.response.content_type = "application/json"
    return json.dumps([
        {**p, "stock_human": humanize.intword(p["stock"])} for p in PRODUCTS
    ])


@bottle.route("/table")
def table():
    bottle.response.content_type = "text/plain"
    return tabulate.tabulate(
        [(p["id"], p["name"], humanize.intcomma(p["stock"])) for p in PRODUCTS],
        headers=["id", "name", "stock"],
    ) + "\n"


if __name__ == "__main__":
    print("Inventory service started on port 8080", flush=True)
    bottle.run(host="0.0.0.0", port=8080, quiet=True)
