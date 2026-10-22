import json
import time

import bottle
import humanize

START = time.time()


@bottle.route("/")
def greet():
    name = bottle.request.query.get("name") or "World"
    bottle.response.content_type = "application/json"
    return json.dumps({
        "message": f"Hello, {name}!",
        "uptime": humanize.naturaldelta(time.time() - START),
    })


if __name__ == "__main__":
    print("Greeter service started on port 8080", flush=True)
    bottle.run(host="0.0.0.0", port=8080, quiet=True)
