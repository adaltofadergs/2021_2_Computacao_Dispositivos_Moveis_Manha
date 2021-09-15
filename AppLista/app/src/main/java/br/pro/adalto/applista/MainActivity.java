package br.pro.adalto.applista;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lvProdutos;
    private List<Produto> listaDeProdutos;
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, FormularioActivity.class);
                intent.putExtra("acao", "inserir");
                startActivity( intent );
            }
        });

        lvProdutos = findViewById(R.id.lvProdutos);

        lvProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent( MainActivity.this, FormularioActivity.class);
                intent.putExtra("acao", "editar");
                startActivity( intent );
            }
        });

        carregarLista();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        carregarLista();
    }

    private void carregarLista(){
        // bando de dados Fake
//        Produto p1 = new Produto("Coca-cola 2L", "Bebidas");
//        Produto p2 = new Produto("Pepsi 1.5L", "Bebidas");
//        Produto p3= new Produto("Trakinas", "Alimentos");
//        listaDeProdutos = new ArrayList<>();
//        listaDeProdutos.add(p1);
//        listaDeProdutos.add(p2);
//        listaDeProdutos.add(p3);

        listaDeProdutos = ProdutoDAO.getProdutos(this);

        if( listaDeProdutos.size() == 0 ){
            Produto fake = new Produto("Lista Vazia ", "");
            listaDeProdutos.add( fake );
            lvProdutos.setEnabled(false);
        }else {
            lvProdutos.setEnabled(true);
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDeProdutos);
        lvProdutos.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}