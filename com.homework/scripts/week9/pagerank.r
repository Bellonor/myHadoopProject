#pages<-read.csv("page",header=FALSE);
pages<-read.csv("people.csv",header=FALSE);
#构造邻接矩阵(方阵):
mrow<-max(pages)
A<-matrix(0,nrow=mrow,ncol=mrow);
    #cols=length(pages[1,]);
rows=length(pages[,1]);
 for(i in 1:rows){
    p1<-pages[i,1];
    p2<-pages[i,2];
    A[p2,p1]<-1;
  }


#考虑阻尼系统的情况
csum<-colSums(A);
csum[csum==0] <- 1;
Arow=nrow(A);
d<-0.85;
de<-1-d/Arow;
delta <- (1-d)/Arow;
B <- matrix(delta,nrow(A),ncol(A));
for (i in 1:Arow) B[i,] <- B[i,] + d*A[i,]/csum;
# 迭代求解特征向量值
x <- rep(1,Arow);
for (i in 1:100) x <- B %*% x
x/sum(x)










#转换为概率矩阵(转移矩阵),不考虑阻尼系统
csum<-colSums(A);
csum[csum==0] <- 1;
Arow=nrow(A);
for(i in 1:Arow){
  A[i,]<-A[i,]/csum;
}














#利用幂法求解特征向量，不考虑阻尼系统的情况
x <- rep(1,Arow);
for (i in 1:10) x <- A %*% x
#除以一个常数
x/sum(x);




