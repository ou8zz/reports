FROM swr.cn-east-2.myhuaweicloud.com/yb7/product-reports-libs:1.0.4

ENV TZ=Asia/Shanghai

COPY reports/META-INF /reports/META-INF
COPY reports/BOOT-INF/classes/com reports/BOOT-INF/classes/com
COPY reports/BOOT-INF/classes/jasper reports/BOOT-INF/classes/jasper
COPY reports/BOOT-INF/classes/fonts.xml reports/BOOT-INF/classes/fonts.xml
COPY reports/BOOT-INF/classes/jasperreports.properties reports/BOOT-INF/classes/jasperreports.properties
COPY reports/BOOT-INF/classes/jasperreports_extension.properties reports/BOOT-INF/classes/jasperreports_extension.properties
COPY logback.xml reports/BOOT-INF/classes/logback.xml
COPY application.properties reports/BOOT-INF/classes/application.properties

WORKDIR reports
RUN mkdir tmp

CMD ["java", "org.springframework.boot.loader.JarLauncher"]