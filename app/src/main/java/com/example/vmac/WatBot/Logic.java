package com.example.vmac.WatBot;

/**
 * Created by danny on 2017-02-11.
 */

public class Logic implements MessageListener{
    MessageSession ms;
    ServerComms sc;
    String newmsg;
    int step=1;
    WordtoNumber wn;

    public Logic(MessageSession ms) {
        this.ms = ms;
        newmsg = null;
        step = 1;
        wn = new WordtoNumber();
    }

    public void restart(){
        step=1;
        updateMsg("");
    }

    public void updateMsg(String msg) {
        newmsg = msg;
        query_start();
    }

    public void query_start(){

        System.out.println("new query starts");
        newmsg = newmsg.trim();

        if(newmsg.equals("restart")){ restart(); return; }

        if(step==1) {
            ms.messageFromBot("Please select your group.\n These are the groups available.\n Say \"help\" for help.");
            step++;
        }
        else if(step==2) { // 그룹 번호 받아오기
            //todo 총 그룹수 받아와서 작은지 확인하기
            if(newmsg.equals("help")) {
                ms.messageFromBot("Help : Selecting the Group\n --------------------\n Say the index of the group you want to command.");
                return;
            }
            else if(wn.word_to_number(newmsg)>0){
            ms.messageFromBot("Please tell a command.");
            step++; }
            else
                ms.messageFromBot("The group doesn't exist. Please try again");


        }
        else if(step==3) {
            if(newmsg.equals("help")) {
                ms.messageFromBot("Help : Available Commands\n ---------------------\n" +
                        "ask task list : FamiVoice shows the list of all tasks.\n" +
                        "ask task mine : FamiVoice shows the list of tasks given to you.\n" +
                        "update task add (new task) : FamiVoice adds new task to the list.\n" +
                        "update task delete (index) : FamiVoice deletes the task of the given index from the list.\n" +
                        "ask location all : FamiVoice shows the connection to the home wi-fi network of every member in the group.\n" +
                        "ask location (member) : FamiVoice shows the connection to the home wi-fi network of the given member.\n" +
                        "update location on : FamiVoice notifies the connection/disconnection to the home wi-fi network.\n" +
                        "update location off : FamiVoice no longer notifies the connection/disconnection to the home wi-fi network.");
                return;
            }
            /*
            ask task list
            ask task mine
            update task add ~~~~
            update task delete (num)
            ask location all
            ask location (someone)
            update location on/off
             */
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
                else if(command[2].equals("of")){}  // todo 문자 정보기능 껴고 키기
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
                    for(int i=4;i<len;i++) task.append(command[i]); // 그 뒤로 이어진 단어들을 연결
                    sc.addTask(task.toString()); // 말한것을 할일에 추가
                }
                else  if(command[2].equals("delete")){

                    int n=0;
                    StringBuffer num = new StringBuffer(command[3]);
                    for(int i=4;i<len;i++) num.append(command[i]); // 그 뒤로 이어진 단어들을 연결
                    n=wn.word_to_number(num.toString());
                    if(n<=1){   ms.messageFromBot("The number is not available. Please try again"); return;}
                    sc.deleteTask(n); // 말한 숫자 번째의 할 일 지우기
                    // todo 총 할일 수보다 큰 숫자인지 확인

                }
                else {   ms.messageFromBot("The command is not available. Please try again"); return;}
            }

            // todo 서버에서 받은 정보 출력하기
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
