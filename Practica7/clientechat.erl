-module(clientechat).
-export([logon/1, logoff/0, broadcast/1, client/2, new_publisher/0]).

%%% Change the function below to return the name of the node where the
%%% servidorchat runs
server_node() ->
    server@Nymph.

    %%%pto0110.
%%% User Commands
logon(Name) ->
    case whereis(mess_client) of 
        undefined ->
            register(mess_client, 
                     spawn(clientechat, client, [server_node(), Name])),
            register(mess_publisher,
                     spawn(clientechat, new_publisher,[]));
        _ -> already_logged_on
    end.

logoff() ->
    mess_client ! logoff,
    mess_publisher ! logoff.

broadcast(Message) ->
    case whereis(mess_client) of % Test if the client is running
        undefined ->
            not_logged_on;
        _ -> mess_client ! {message_all, Message},
             ok
end.
%%% The publisher 
new_publisher() ->
    publisher([]).

publisher(Message_List) ->
    receive
        logoff ->
            exit(normal);
        {publish_m,FromName,Message,Message_Counter} ->
            New_Message_List = 
                lists:append([{FromName,Message,Message_Counter}],Message_List),
            publisher(New_Message_List)
    after 3000 ->
        case Message_List of
            [] -> 
                publisher(Message_List);
            _ ->
                {FromName,Message,Message_Counter} = minCounter(Message_List),
                New_Message_List = del_minCounter(Message_List,Message_Counter),
                io:format("Message from ~p(~p): ~p~n", [FromName, Message_Counter, Message]),
                publisher(New_Message_List)
        end
    end.

minCounter([{N,M,MC}|ML]) ->
    aux_minCounter(ML,{N,M,MC}).
aux_minCounter([],Acc) ->
    Acc;
aux_minCounter([{N,M,MC}|ML],{_,_,AccMC})
    when MC < AccMC ->
        aux_minCounter(ML,{N,M,MC});
aux_minCounter([_|ML],Acc) ->
    aux_minCounter(ML,Acc).

del_minCounter([],_) -> [];
del_minCounter([{_,_,C}|ML],MC)
    when C == MC ->
        ML;
del_minCounter([{N,M,C}|ML],MC) ->
    [{N,M,C}|del_minCounter(ML,MC)].

%%% The client process which runs on each user node
client(Server_Node, Name) ->
    {servChat, Server_Node} ! {self(), logon, Name},
    await_result(),
    client(Server_Node).

client(Server_Node) ->
    receive
        logoff ->
            exit(normal);
        {message_all, Message} ->
            {servChat, Server_Node} ! {self(), message_all, Message},
            await_result();
        {message_from, FromName, Message,Message_Counter} ->
            mess_publisher ! {publish_m,FromName,Message,Message_Counter}
    end,
    client(Server_Node).

%%% wait for a response from the server
await_result() ->
    receive
        {clientechat, stop, Why} -> % Stop the client 
            io:format("~p~n", [Why]),
            exit(normal);
        {clientechat, What} ->  % Normal response
            io:format("~p~n", [What])
    after 5000 ->
            io:format("No response from server~n", []),
            exit(timeout)
    end.
