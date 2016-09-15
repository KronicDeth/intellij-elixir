defmodule InfraTg.Query.AssociateUserNotification.Crud do
  import Ecto.Query
  alias InfraTg.Schema
  alias Schema.AssociateUserNotification
  alias Schema.Notification

  def list_messages do
    from associate_user_notification in AssociateUserNotification,
    inner_join notifications, assoc<caret>   # <- **_wrong syntax raises this error_**
  end
end
