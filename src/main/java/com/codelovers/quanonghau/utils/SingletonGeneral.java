package com.codelovers.quanonghau.utils;

public class SingletonGeneral {
     private  static SingletonGeneral _instance = null;
    private  SingletonGeneral(){
  }
   public static SingletonGeneral getInstance(){
        if(_instance == null){
            _instance = new SingletonGeneral();
        }
        return  _instance;
   }
}
