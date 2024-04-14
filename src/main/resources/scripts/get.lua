request = function()
    headers = {}
    headers["token"] = "admin_token"
    return wrk.format("GET", "/banner", headers)
end