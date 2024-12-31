--[[
    签到功能的实现
    1、当天没有进行签到时，则签到成功，否则签到失败
    2、签到成功后，返回总签到天数和连续签到天数
]]--
local key = KEYS[1]
local offset = tonumber(ARGV[1])
-- 当天签到状态
local current = redis.call("GETBIT", key, offset)
if current == 1 then
    -- 已经签到了
    return {0, 0, 0}
end

-- 如果未签到，进行签到
redis.call("SETBIT", key, offset, 1)

-- 重新计算总签到天数
local totalDays = redis.call("BITCOUNT", key)

-- 重新计算连续签到天数
local continuousDays = 0
for i = offset, 0, -1 do
    if redis.call("GETBIT", key, i) == 1 then
        continuousDays = continuousDays + 1
    else
        break
    end
end

return {1, totalDays, continuousDays}  -- 1 表示签到成功