Repo.one((from r in Role, where: r.name == "admin", select: r.id), log: false)
