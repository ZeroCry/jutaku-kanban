# jūtaku-kanban （住宅カンバン）

Get offers from *immobilienscout24.de* directly to your Trello board.

Each offer shows up neatly in a card, with the direct link to it in the card
description. Seen offers are tracked in text files and everything is
configured through command line args–you can

* aggregate multiple searches in one list,
* do the same in multiple lists,
* run searches independently,

all while staying up to date via cron :)


## Setup

First, get yourself a [Trello API key](https://trello.com/app-key).

Use that key to generate a token. Visit
https://trello.com/1/authorize?expiration=never&scope=read,write&response_type=token&name=jutaku-kanban&key=API_KEY (replace your key in the URL).

Pick a board. Navigate to it on Trello and get the board ID from the URL in
your browser. It looks like https://trello.com/b/BOARD_ID/...

Finally, get the ID of the list in which new entries should show up. Open
https://api.trello.com/1/boards/BOARD_ID/lists?key=API_KEY&token=API_TOKEN
and pick the `id` of the list you want.


## Usage

Refer to [setup](#setup) for the first three arguments. The program uses a
simple text file–`SEEN`–to keep track of seen offers. Pass it a search `URL`
directly from any *immobilienscout24.de* results page.

    $ java -jar jutaku-kanban-1.0.0-standalone.jar API_KEY API_TOKEN LIST_ID SEEN URL

**Note:** don't use the same file for different searches for now.

## Issues

* Cards created via the Trello API don't show up in the iOS app (they do on the
  web-app though).
