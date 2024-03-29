-module(servidorchat).
-export([start_server/0, server/0, entregarMensaje/4]).

%%% This is the server process for the "messenger"
%%% the user list has the format [{ClientPid1, Name1},{ClientPid22, Name2},...]
server() ->
    process_flag(trap_exit, true),
    server([],0).

server(User_List,Message_Counter) ->
    receive
        {From, logon, Name} ->
            New_User_List = server_logon(From, Name, User_List),
            io:format("list is now: ~p~n", [New_User_List]),
            server(New_User_List,Message_Counter);
        {'EXIT', From, _} ->
            New_User_List = server_logoff(From, User_List),
            server(New_User_List,Message_Counter);
    	{From, message_all, Message} ->
            New_Message_Counter = incrementa(Message_Counter),
            spawn(servidorchat,entregarMensaje,[User_List,From,Message,New_Message_Counter]),
            
            io:format("Message_Counter is now: ~p~n", [New_Message_Counter]),
            server(User_List,New_Message_Counter)
    end.

entregarMensaje(User_List,From,Message,Message_Counter) ->
    [ToPid ! {server_transfer(From, To, Message,Message_Counter, User_List)} || {ToPid, To} <- User_List].

incrementa(V) ->
    V + 1.

%%% Start the server
start_server() ->
    register(servChat, spawn(servidorchat, server, [])).

%%% Server adds a new user to the user list
server_logon(From, Name, User_List) ->
    %% check if logged on anywhere else
    case lists:keymember(Name, 2, User_List) of
        true ->
            From ! {clientechat, stop, user_exists_at_other_node},  %reject logon
            User_List;
        false ->
            From ! {clientechat, logged_on},
            link(From),
            [{From, Name} | User_List]        %add user to the list

    end.

%%% Server deletes a user from the user list
server_logoff(From, User_List) ->
    lists:keydelete(From, 1, User_List).

%%% Server transfers a message between user
server_transfer(From, To, Message, Message_Counter, User_List) ->
    %% check that the user is logged on and who he is
    case lists:keysearch(From, 1, User_List) of
        false ->
            From ! {clientechat, stop, you_are_not_logged_on};
        {value, {_, Name}} ->
            case string:equal(Name,To) of
                false ->
                    server_transfer(From, Name, To, Message, Message_Counter, User_List);
                true ->
                    From ! {clientechat, sent}
            end
    end.

%%% If the user exists, send the message
server_transfer(From, Name, To, Message, Message_Counter, User_List) ->
    %% Find the receiver and send the message
    case lists:keysearch(To, 2, User_List) of
        false ->
            From ! {clientechat, receiver_not_found};
        {value, {ToPid, To}} ->
            ToPid ! {message_from, Name, Message, Message_Counter}, 
            From ! {clientechat, sent}
    end.