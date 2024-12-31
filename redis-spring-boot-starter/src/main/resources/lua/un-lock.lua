-- 分布式锁解锁操作
-- 只有锁的值和解锁传的值相同，才能正确解锁
local lockKey = KEYS[1]
local lockVal = ARGV[1]

if redis.call('get', lockKey) == lockVal then
    return redis.call('del', lockKey)
else
    return 0
end