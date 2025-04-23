-- 缓存2是以缓存1的结果构建的
local key = KEYS[1]
local infoKeyPrefix = KEYS[2]
local name = ARGV[1]

-- 根据用户名称缓存找到用户的ID
local cacheKey = key .. name
local result = redis.call('GET', cacheKey)
if not result then
    -- 没有数据，返回null
    return nil
end

-- 真实缓存key
local trueResultCacheKey = infoKeyPrefix .. result
local trueResult = redis.call('GET', trueResultCacheKey)
if not trueResult then
    return nil
end

return trueResult