#说明

- 用于HJJ项目中，部署于地面站其中一个席位
- 接收地面GPS差分码（TCP client），转发至多个GDC客户端（TCP server）
- 支持失败重试、断线重连
- 软件架构见'软件架构.jpg'