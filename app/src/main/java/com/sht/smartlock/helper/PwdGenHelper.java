package com.sht.smartlock.helper;

import java.util.Random;

/**
 * Created by Administrator on 2015/11/24.
 */
public class PwdGenHelper {

    private Random rdseed=new Random();

    /**
     *@param
     *length        password length;长度
     *@param
     *letters       boolean non-capital letters combination control; 小写
     *@param
     *letters_capi  boolean capital letters combination control;大写
     *@param
     *numbers       numbers control;数字
     */
    public  String getpwd(int length,boolean letters,boolean letters_capi,boolean numbers){
        StringBuilder sb=new StringBuilder();
        Random rd=this.rdseed;
        while(length-->0){
            char _1dgt=0;
            if(letters&&letters_capi&&numbers){
                int _key=rd.nextInt(3);
                if(2==_key){
                    _1dgt=get_L();
                }else if(1==_key){
                    _1dgt=get_L_C();
                }else if(0==_key){
                    _1dgt=get_N();
                }
            }else if(letters&&letters_capi&&!numbers){
                int _key=rd.nextInt(2);
                if(1==_key){
                    _1dgt=get_L();
                }else if(0==_key){
                    _1dgt=get_L_C();
                }
            }else if(!letters&&letters_capi&&numbers){
                int _key=rd.nextInt(2);
                if(1==_key){
                    _1dgt=get_N();
                }else if(0==_key){
                    _1dgt=get_L_C();
                }
            }else if(letters&&!letters_capi&&numbers){
                int _key=rd.nextInt(2);
                if(1==_key){
                    _1dgt=get_L();
                }else if(0==_key){
                    _1dgt=get_N();
                }
            }else if(letters&&!letters_capi&&!numbers){
                _1dgt=get_L();
            }else if(!letters&&!letters_capi&&numbers){
                _1dgt=get_N();
            }else if(!letters&&letters_capi&&!numbers){
                _1dgt=get_L_C();
            }else{
                break;
            }

            sb.append(_1dgt);
        }
        return sb.toString() ;
    }
    //小写
    private char get_L_C(){
        Random rd=this.rdseed;
        int _dgt=(rd.nextInt(26)+65);
        return (char)_dgt;
    }
    //数字
    private char get_N(){
        Random rd=this.rdseed;
        int _dgt=(rd.nextInt(10)+48);
        return (char)_dgt;
    }
    //小写
    private char get_L(){
        Random rd=this.rdseed;
        int _dgt=(rd.nextInt(26)+97);
        return (char)_dgt;
    }
//    public static void main(String[] args) {
//        PwdGenHelper test= new PwdGenHelper();
//
//        int testct=10000000;//千万次测试
//
//        long start=System.nanoTime();
//        while(testct-->0){
//            test.getpwd(10, true, true, true);
//        }
//        long end= System.nanoTime();
//        System.out.println(((double)(end-start))/(1000000000));
//        System.out.println(test.getpwd(10, true, true, true));
//    }
}
