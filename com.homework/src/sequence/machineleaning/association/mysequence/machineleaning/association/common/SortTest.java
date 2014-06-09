package mysequence.machineleaning.association.common;

import java.util.Comparator;             
import java.util.List;             
import java.util.ArrayList;             
import java.util.Collections;             
             
class User {             
 String name;             
 String age;             
             
 public User(String name,String age){             
  this.name=name;             
  this.age=age;             
 }             
 public String getAge() {             
  return age;             
 }             
 public void setAge(String age) {             
  this.age = age;             
 }             
 public String getName() {             
  return name;             
 }             
 public void setName(String name) {             
  this.name = name;             
 }              
}             
             
class ComparatorUser implements Comparator{             
             
 public int compare(Object arg0, Object arg1) {             
  User user0=(User)arg0;             
  User user1=(User)arg1;             
  //首先比较年龄，如果年龄相同，则比较名字             
  int flag=user0.getAge().compareTo(user1.getAge());             
  if(flag==0){             
   return user0.getName().compareTo(user1.getName());             
  }else{             
   return flag;             
  }               
 }             
             
}             
             
public class SortTest {             
             
             
 public static void main(String[] args){             
  List userlist=new ArrayList();             
  userlist.add(new User("dd","4"));             
  userlist.add(new User("aa","1"));             
  userlist.add(new User("ee","5"));             
  userlist.add(new User("bb","2"));               
  userlist.add(new User("ff","5"));             
  userlist.add(new User("cc","3"));             
  userlist.add(new User("gg","6"));             
               
  ComparatorUser comparator=new ComparatorUser();             
  Collections.sort(userlist, comparator);             
                
  for (int i=0;i<userlist.size();i++){             
   User user_temp=(User)userlist.get(i);             
      System.out.println(user_temp.getAge()+","+user_temp.getName());              
  }             
               
 }             
}             
