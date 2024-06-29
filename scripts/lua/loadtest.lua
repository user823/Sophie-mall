-- 脚本接受命令行的额外参数
-- method: get、post
-- arg1: xxx
-- arg2: xxx
-- ...
-- 如果是get，后续的arg会拼接成路径参数；否则放入到请求体中
local json = require "json"

counter = 1
threads = {}

-- 设置请求头
wrk.headers["Content-Type"] = "application/json"

function setup(thread)
    -- 给每个线程设置一个 id 参数
    thread:set("id", counter)
    -- 将线程添加到 table 中
    table.insert(threads, thread)
    counter = counter + 1
end

function init(args)
    -- 初始化计数器和成功请求数
    requests  = 0
    responses = 0
    ok = 0

    -- 收集额外参数
    local extra_arg = {}
    for i, arg in ipairs(args) do
        local key, value = arg:match("(%w+)=(.*)")
        if key and value then
            if key == "method" then
                wrk.method = value
            elseif value:sub(1,1) == "[" then
                extra_arg[key] = json.decode(value)
            else
                extra_arg[key] = value
            end
        end
    end

    -- 构造请求参数
    if wrk.method == "GET" then
        wrk.path = wrk.path .. "?" .. createPathArgs(extra_arg)
    else
        wrk.body = createJsonArgs(extra_arg)
    end
end

function request()
    -- 每发起一次请求 +1
    requests = requests + 1
    return wrk.format(wrk.method, wrk.path, wrk.headers, wrk.body)
end

function response(status, headers, body)
    -- 每得到一次请求的响应 +1
    responses = responses + 1
    if status == 200 then
        ok = ok + 1
    end
end

function done(summary, latency, requests)
    local totalOk = 0
    local totalReq = 0
    -- 输出每个线程的统计信息
    for index, thread in ipairs(threads) do
        local id        = thread:get("id")
        local requests  = thread:get("requests")
        local responses = thread:get("responses")
        local ok = thread:get("ok")
        totalOk = totalOk + ok
        totalReq = totalReq + requests
        local msg = "thread %d made %d requests and got %d responses, success requests %d, fail requests %d, success ratio %.3f"
        print(msg:format(id, requests, responses, ok, requests - ok, ok/requests))
    end

    local msg = "all threads avg success ratio is %.3f"
    print(msg:format(totalOk/totalReq))
end

function createPathArgs(args)
    local pathArgs = ""
    for k, v in pairs(args) do
        pathArgs = pathArgs .. k .. "=" .. v .. "&"
    end
    pathArgs = string.sub(pathArgs, 1, -2)
    return pathArgs
end

function createJsonArgs(args)
    return json.encode(args)
end
