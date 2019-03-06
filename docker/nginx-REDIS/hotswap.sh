#!/usr/bin/env bash
PORT=':6379'
BEGIN='    server '
SEMI=';'
PARAM_new="$BEGIN$1$PORT$SEMI"

if grep -xq "$PARAM_new" /etc/nginx/nginx.conf
then
	printf "%s\n" "Our database have already been pointing to $1$PORT. No reload on nginx is needed!!!"
else
	sed -i'' "s/^$BEGIN.*$/$PARAM_new/" /etc/nginx/nginx.conf
	/usr/sbin/nginx -s reload
fi
