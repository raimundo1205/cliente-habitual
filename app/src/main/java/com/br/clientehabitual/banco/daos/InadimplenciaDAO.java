package com.br.clientehabitual.banco.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.br.clientehabitual.banco.GerenciarBanco;
import com.br.clientehabitual.models.Cliente;
import com.br.clientehabitual.models.Inadimplencia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InadimplenciaDAO {
    private SQLiteDatabase banco;
    private GerenciarBanco gerenciarBanco;
    private static final String[] campos = {"id", "dataCriacao", "dataBaixa", "clienteId", "situacao"};
    private static final String nomeTabela = "inadimplencias";

    public InadimplenciaDAO(Context context) {
        gerenciarBanco = new GerenciarBanco(context);
    }
    public Inadimplencia newInadimplencia(Inadimplencia inadimplencia){
        Cliente cliente = inadimplencia.getCliente();
        banco = gerenciarBanco.getWritableDatabase();
        try{
            ContentValues dados = new ContentValues();
            dados.put(campos[1], calendarToString(inadimplencia.getDataInicio()));
            dados.put(campos[3], cliente.getId());
            dados.put(campos[4], "false");
            inadimplencia.setId(banco.insert(nomeTabela,null,dados));
            banco.close();
        } catch (Exception e){}
        return inadimplencia;
    }
    /*listInadimplecias !!!! PROVAVELMENTE NÃO SERA USADA*/
    public List<Inadimplencia> listInadimplecias(){
        Inadimplencia inadimplencia = null;
        List<Inadimplencia> inadimplencias = new ArrayList<>();
        SQLiteDatabase db = gerenciarBanco.getReadableDatabase();
        Cursor cursor = db.query(nomeTabela, campos,null, null,null,null,null);
        while (cursor.moveToNext()) {
            Cliente cliente = new Cliente(cursor.getInt(3), "", "");
            inadimplencia.setCliente(cliente);
            inadimplencia.setDataInicio(stringToCalendar(cursor.getString(1)));
            if (cursor.getString(2).length() > 0) {
                inadimplencia.setDataFim(stringToCalendar(cursor.getString(2)));
            }
            inadimplencia.setId(cursor.getInt(0));
            inadimplencia.setQuitada(Boolean.getBoolean(cursor.getString(4)));
            inadimplencias.add(inadimplencia);
        }
        return inadimplencias;
    }public List<Inadimplencia> listInadimpleciasCliente(Cliente cliente){
        Inadimplencia inadimplencia = null;
        List<Inadimplencia> inadimplencias = new ArrayList<>();
        SQLiteDatabase db = gerenciarBanco.getReadableDatabase();
        String where = campos[3] + " = "+cliente.getId();
        Cursor cursor = db.query(nomeTabela, campos,where, null,null,null,null);
        while (cursor.moveToNext()) {
            inadimplencia.setCliente(cliente);
            inadimplencia.setDataInicio(stringToCalendar(cursor.getString(1)));
            if (cursor.getString(2).length() > 0) {
                inadimplencia.setDataFim(stringToCalendar(cursor.getString(2)));
            }
            inadimplencia.setId(cursor.getInt(0));
            inadimplencia.setQuitada(Boolean.getBoolean(cursor.getString(4)));
            inadimplencias.add(inadimplencia);
        }
        return inadimplencias;
    }
    public void quitInadimplencia(Inadimplencia inad){
        SQLiteDatabase db = gerenciarBanco.getReadableDatabase();
        String where = campos[0] + " = "+ inad.getId();
        ContentValues dados = new ContentValues();
        dados.put(campos[1], calendarToString(inad.getDataFim()));
        dados.put(campos[4], Boolean.toString(inad.isQuitada()));
        db.update(nomeTabela,dados,where,null);
        db.close();
    }
    public void deleteInadimplencia(Inadimplencia inad) {
        SQLiteDatabase db = gerenciarBanco.getReadableDatabase();
        String where = campos[0] + " = " + inad.getId();
        db.delete(nomeTabela, where, null);
        db.close();
    }
    public String calendarToString(Calendar calendar){
        String data = "";
        calendar.getTime();
        try {
            SimpleDateFormat dataSimple = new SimpleDateFormat("dd-MM-yyyy");
            data = dataSimple.format(calendar);
        }catch (Exception e){}
        return data;
    }
    public Calendar stringToCalendar(String dataString){
        Calendar data = Calendar.getInstance();
        SimpleDateFormat dataSimple = new SimpleDateFormat("dd-MM-yyyy");
        try {
            data.setTime(dataSimple.parse(dataString));
        }catch (Exception e){}
        return data;
    }
}
