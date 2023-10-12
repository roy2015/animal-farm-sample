使用说明：
0. swagger地址
   http://localhost:8083/swagger-ui/index.html
   
1. 浏览器里同步家谱数据：
   http://localhost:8083/api/syncTreeData
   
2. 所有成员
   http://localhost:8083/api/list

2. 浏览器里打开：
   file:///Users/apple/guojun/code/front-end-code/my-family-tree-1/v1/index.html

3. 删除一个成员
curl -X DELETE "http://localhost:8083/api/211" -H "accept: application/json"
   
4. 新增一个成员
curl -X POST "http://localhost:8083/api/add_family_person" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"code\":\"1132231112\",\"mark\":\"萌\",\"name\":\"培萌\",\"parentCode\":113223111,\"parentName\":null,\"remark\":null,\"sort\":1}"