# Sử dụng image PostgreSQL chính thức
FROM postgres:latest

# Thiết lập biến môi trường PostgreSQL
ENV POSTGRES_USER=demo
ENV POSTGRES_PASSWORD=demo
ENV POSTGRES_DB=demo

# Sao chép file SQL vào thư mục khởi tạo PostgreSQL
COPY src/main/resources/demo.sql /docker-entrypoint-initdb.d/

# Mở cổng PostgreSQL
EXPOSE 5432