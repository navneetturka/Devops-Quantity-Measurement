FROM ubuntu:latest
LABEL authors="navne"

ENTRYPOINT ["top", "-b"]