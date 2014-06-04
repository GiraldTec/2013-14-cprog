%%% Message passing utility.  
%%% User interface:
%%% login(Name)
%%%     One user at a time can log in from each Erlang node in the
%%%     system messenger: and choose a suitable Name. If the Name
%%%     is already logged in at another node or if someone else is
%%%     already logged in at the same node, login will be rejected
%%%     with a suitable error message.
%%% logoff()
%%%     Logs off anybody at at node
%%% message(ToName, Message)
%%%     sends Message to ToName. Error messages if the user of this 
%%%     function is not logged on or if ToName is not logged on at
%%%     any node.
%%%
%%% One node in the network of Erlang nodes runs a server which maintains
%%% data about the logged on users. The server is registered as "messenger"
%%% Each node where there is a user logged on runs a client process registered
%%% as "mess_client" 
%%%
%%% Protocol between the client processes and the server
%%% ----------------------------------------------------
%%% 
%%% To server: {ClientPid, logon, UserName}
%%% Reply {messenger, stop, user_exists_at_other_node} stops the client
%%% Reply {messenger, logged_on} logon was successful
%%%
%%% When the client terminates for some reason
%%% To server: {'EXIT', ClientPid, Reason}
%%%
%%% To server: {ClientPid, message_to, ToName, Message} send a message
%%% Reply: {messenger, stop, you_are_not_logged_on} stops the client
%%% Reply: {messenger, receiver_not_found} no user with this name logged on
%%% Reply: {messenger, sent} Message has been sent (but no guarantee)
%%%
%%% To client: {message_from, Name, Message},
%%%
%%% Protocol between the "commands" and the client
%%% ---------------------------------------------- 
%%%
%%% Started: messenger:client(Server_Node, Name)
%%% To client: logoff
%%% To client: {message_to, ToName, Message}
%%%
%%% Configuration: change the server_node() function to return the
%%% name of the node where the messenger server runs

-module(servidorchat).
-export([start_server/0, server/0]).

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
    	    [ToPid ! {server_transfer(From, To, Message, User_List)} || {ToPid, To} <- User_List],
            New_Message_Counter = incrementa(Message_Counter),
            io:format("Message_Counter is now: ~p~n", [New_Message_Counter]),
            server(User_List,New_Message_Counter)
    end.

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
server_transfer(From, To, Message, User_List) ->
    %% check that the user is logged on and who he is
    case lists:keysearch(From, 1, User_List) of
        false ->
            From ! {clientechat, stop, you_are_not_logged_on};
        {value, {_, Name}} ->
            case string:equal(Name,To) of
                false ->
                    server_transfer(From, Name, To, Message, User_List);
                true ->
                    From ! {clientechat, sent}
            end
    end.

%%% If the user exists, send the message
server_transfer(From, Name, To, Message, User_List) ->
    %% Find the receiver and send the message
    case lists:keysearch(To, 2, User_List) of
        false ->
            From ! {clientechat, receiver_not_found};
        {value, {ToPid, To}} ->
            ToPid ! {message_from, Name, Message}, 
            From ! {clientechat, sent}
    end.