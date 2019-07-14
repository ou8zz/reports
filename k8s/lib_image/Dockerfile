FROM openjdk:8u151-jdk-alpine3.7

RUN echo $'http://mirrors.aliyun.com/alpine/v3.7/main\n\
http://mirrors.aliyun.com/alpine/v3.7/community' > /etc/apk/repositories

# 解决jasperreport无法启动的问题
# https://github.com/docker-library/openjdk/issues/73
# RUN apk --update add fontconfig ttf-dejavu
RUN apk --update --no-cache add msttcorefonts-installer fontconfig && update-ms-fonts && fc-cache -f

COPY reports /reports