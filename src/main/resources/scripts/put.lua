counter = 0

request = function()
    headers = {}
    headers["token"] = "admin_token"
    headers["Content-Type"] = "application/json"
    counter = counter + 1
    return wrk.format("POST", "/banner", headers, "{\"tag_ids\":[" .. counter .."], \"feature_id\":" .. counter ..", \"body\":\"{\\\"text\\\":\\\"abacaba\\\"}\", \"is_active\":true}")
end