#!/usr/bin/env python3
"""Capture réseau limitée à un hôte et à des ports autorisés.

Usage exemple :
    sudo python3 sniffer.py --interface wlan0 --host 10.0.0.1

Le script affiche uniquement les métadonnées des paquets et ne sauvegarde
pas leur contenu. Utilisez-le uniquement dans un périmètre autorisé.
"""

import argparse
from datetime import datetime

from scapy.all import IP, TCP, UDP, sniff

DEFAULT_PORTS = (22, 23, 220, 225)


def parse_args():
    parser = argparse.ArgumentParser(description="Capture réseau ciblée de métadonnées")
    parser.add_argument("--interface", default="wlan0", help="Interface réseau à écouter")
    parser.add_argument("--host", default="10.0.0.1", help="Adresse IP ciblée")
    parser.add_argument(
        "--ports",
        nargs="+",
        type=int,
        default=list(DEFAULT_PORTS),
        help="Ports TCP/UDP à surveiller",
    )
    parser.add_argument("--count", type=int, default=0, help="Nombre de paquets (0 = continu)")
    return parser.parse_args()


def packet_filter(host, ports):
    port_filter = " or ".join(f"port {port}" for port in ports)
    return f"host {host} and ({port_filter})"


def display_metadata(packet):
    if IP not in packet:
        return

    ip = packet[IP]
    transport = packet.getlayer(TCP) or packet.getlayer(UDP)
    protocol = transport.name if transport else ip.proto
    source_port = getattr(transport, "sport", "-")
    destination_port = getattr(transport, "dport", "-")
    packet_length = len(packet)
    timestamp = datetime.now().isoformat(timespec="seconds")

    print(
        f"[{timestamp}] {ip.src}:{source_port} -> "
        f"{ip.dst}:{destination_port} {protocol} ({packet_length} octets)"
    )


def main():
    args = parse_args()
    bpf_filter = packet_filter(args.host, args.ports)

    print(f"[*] Interface : {args.interface}")
    print(f"[*] Hôte      : {args.host}")
    print(f"[*] Ports     : {', '.join(map(str, args.ports))}")
    print("[*] Métadonnées uniquement. Ctrl+C pour arrêter.")

    sniff(
        iface=args.interface,
        filter=bpf_filter,
        prn=display_metadata,
        count=args.count,
        store=False,
    )


if __name__ == "__main__":
    main()
