package lila.msg

import org.joda.time.DateTime

import lila.user.User

case class Msg(
    text: String,
    user: UserId,
    date: DateTime
):

  def asLast =
    Msg.Last(
      text = text take 60,
      user = user,
      date = date,
      read = false
    )

object Msg:

  case class Last(
      text: String,
      user: UserId,
      date: DateTime,
      read: Boolean
  ):
    def unreadBy(userId: UserId) = !read && user != userId

  def make(text: String, user: UserId): Option[Msg] =
    val cleanText = lila.common.String.normalize(text.trim take 8_000)
    cleanText.nonEmpty option Msg(
      text = cleanText,
      user = user,
      date = DateTime.now
    )
