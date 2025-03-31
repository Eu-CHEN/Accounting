<template>
<!--  <nav>-->
<!--    <router-link to="/home">Home</router-link> |-->
<!--    <router-link to="/about">About</router-link>-->
<!--  </nav>-->

  <router-view/>
</template>

<style>
body,html{
  padding: 0;
  margin: 0;
  height: 100%;
  overflow: hidden;
}
.flex-float{
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.flex{
  display: flex;
  align-items: center;
}
.page-content{
  box-sizing: border-box;
  display: block;
  width: 100%;
  padding: 20px;
  background: white;
  margin-top: 20px;
}
.el-main{
  background: #efefef;
  height: 100%;
  overflow: hidden;
}
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}

#app {
  height: 100%;
  overflow: hidden;
}

/* 确保路由视图容器也是全高的 */
.el-container {
  height: 100%;
}

/* 设置侧边栏样式 */
.el-aside {
  height: 100%;
  overflow-y: auto;
}
</style>
