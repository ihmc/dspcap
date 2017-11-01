#!/bin/bash

interface=$1
tcpdump -i $interface -s 6669 -w ./dspro.pcap
