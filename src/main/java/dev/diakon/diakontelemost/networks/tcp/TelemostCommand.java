package dev.diakon.diakontelemost.networks.tcp;

public record TelemostCommand (byte commandId, String payload){}
