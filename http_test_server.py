from http.server import BaseHTTPRequestHandler, HTTPServer
import json


class PentestHttpHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path == "/login":
            content_length = int(self.headers.get("Content-Length", 0))
            post_data = self.rfile.read(content_length)

            print("\n" + "=" * 50)
            print("[!] ALERTE : Trafic HTTP non chiffré reçu dans le laboratoire !")

            try:
                json_data = json.loads(post_data.decode("utf-8"))
                print("[+] Données JSON reçues :")
                print(json.dumps(json_data, indent=4, ensure_ascii=False))
                if "secret_token" in json_data:
                    print("[!] Jeton de test reçu.")
            except (UnicodeDecodeError, json.JSONDecodeError):
                print("[+] Données brutes reçues.")

            print("=" * 50 + "\n")
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.end_headers()
            self.wfile.write(b'{"status":"received"}')
        else:
            self.send_error(404, "Not Found")

    def log_message(self, format, *args):
        return


def run_server(host="127.0.0.1", port=8080):
    httpd = HTTPServer((host, port), PentestHttpHandler)
    print(f"[*] Serveur HTTP de laboratoire actif sur {host}:{port}...")
    print("[*] Ctrl+C pour arrêter.")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n[-] Serveur arrêté.")
    finally:
        httpd.server_close()


if __name__ == "__main__":
    run_server()
