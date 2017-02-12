package com.example.vmac.WatBot;

/**
 * Created by danny on 2017-02-11.
 */

public class Logic implements MessageListener{
    MessageSession ms;
    ServerComms sc;
    String newmsg;
    int step=1;

    public void restart(){
        step=1;
        query_start();
        newmsg="";
        }
    public void query_start(){

        if(newmsg.equals("restart")){ restart(); return; }

        if(step==1) {
            ms.messageFromBot("Please select a group");
            step++;
        }
        else if(step==2) {
            //todo 가능한지 판단하기
            if(false){
            ms.messageFromBot("Please tell a command");
            step++; }
            else
                ms.messageFromBot("The group doesn't exist. Please try agian");


        }
        else if(step==3) {
            String[] command=newmsg.split(" ");
            int len=command.length;

            if(len<3){   ms.messageFromBot("The command is not available. Please try again"); return;}

            if(command[0].equals("ask")&&command[1].equals("location")){

                if(command[2].equals("all")){
                    sc.requestAllLocations();
                }
                else sc.requestSomeoneLocations();

            } //ask location 전체 or 특정인물
            else if(command[0].equals("update")&&command[1].equals("location")){

                if(command[2].equals("on")){}
                else if(command[2].equals("of")){}  // 문자 정보기능 껴고 키기
                else {   ms.messageFromBot("The command is not available. Please try again"); return;}

            }// update location on or off
            else if(command[0].equals("ask")&&command[1].equals("task")){

                if(command[2].equals("list")){ sc.requestAllTasks(); }  // 전부
                else if(command[2].equals("mine")){ sc.requestMyTasks(); }  // 내꺼만
                else {   ms.messageFromBot("The command is not available. Please try again"); return;}

            } // ask task list of mine

            if(len<4){   ms.messageFromBot("The command is not available. Please try again"); return;}

            if(command[0].equals("update")&&command[1].equals("task")){

                if(command[2].equals("add")){
                    StringBuffer task = new StringBuffer(command[3]);
                    for(int i=4;i<len;i++) task.append(command[i]);
                    sc.addTask(task.toString());
                }
                else  if(command[2].equals("delete")){
                    // todo 숫자로 변환
                    int n=0;
                    sc.deleteTask(n);
                }
                else {   ms.messageFromBot("The command is not available. Please try again"); return;}
            }

            // todo 정보 출력
            restart();

        }



    }

    void input(String message){

    }


    @Override
    public void newMessageFromUser(String msg) {

        newmsg=msg;
        query_start();

    }

    @Override
    public void newMessageFromBot(String msg) {

    }


}
