package com.jmsapplay.biblia.view;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Random;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jmsapplay.biblia.R;
import com.jmsapplay.biblia.model.Capitulo;
import com.jmsapplay.biblia.model.Versiculo;


import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.MobileAds;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean backPressed = false;

    // Progress Dialog
    private ProgressDialog pDialog;
    private boolean PanelDialog = true;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    // File url to download
    private static String file_url = "https://raw.githubusercontent.com/jamesonSouza/biblia/master/app/src/main/assets/";
    private static String textToShared = "";
    private static String strCapitulo = "";

    private ListView lv = null;
    private String livro = "";
    private String tituloLivro = "";
    private DrawerLayout drawer = null;
    private MenuItem MIShare = null;
    private ShareActionProvider mShareActionProvider;

    private ArrayList<Capitulo> capitulos;

    private ImageView ivLogo = null;
    private LinearLayout llStart = null;
    private Button btnFrases;
    private TextView txtPalavras;
    String[] palavras = {

    };

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MobileAds.initialize(this, "ca-app-pub-9275202133724780~1832444752");
       /* StartFragment startFragment = new StartFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_main_menu_ft, startFragment);
        fragmentTransaction.commit();*/


        // Anuncios metodos//

        btnFrases = (Button) findViewById(R.id.btnPalavra);
        txtPalavras = (TextView) findViewById(R.id.txtPalavras);

       /* PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);*/
        /*mAdView = (AdView) findViewById(R.id.AdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        prepare();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //     .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        btnFrases.setOnClickListener(new View.OnClickListener() {
            // ArrayAdapter<String> aPalavras = new ArrayAdapter<String>(this,R.layout.content_main, palavras );
            @Override
            public void onClick(View v) {

                String vpalavra = "";
                Random random = new Random();
                int numeroAll = random.nextInt(getPalavras().length);
                txtPalavras.setText(getPalavras()[numeroAll]);


            }
        });


        lv = (ListView) findViewById(R.id.lvLivro);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View rowView,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked", "pos: " + pos);

                TextView textView = (TextView) rowView.findViewById(R.id.txtVersiculo);


                if (textToShared.equals("")) {
                    textToShared = livro.toUpperCase() + " - Cap.:" + strCapitulo + "\n\t";
                }

                textToShared += textView.getText().toString().trim() + "\n\t";

                if (MIShare != null) if (!MIShare.isVisible()) MIShare.setVisible(true);

                mensagemSelecionado();

                return true;
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (!backPressed) {
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
            relativeLayout.setBackgroundResource(R.drawable.actibarperso);

            backPressed = true;
            livro = "";
            limparVoltar();

            if (MIShare != null) MIShare.setVisible(false);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rom, R.id.txtVersiculo, new String[]{""});
            lv.setAdapter(adapter);

            this.setTitle("Bíblia");

            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!livro.equals("")) {
            menu.clear();

            insereMenuItemShare(menu);
            insereCapitulos(QtdeCapituloDoLivro(livro), menu);
        }

        MIShare = menu.findItem(R.id.menu_item_share);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MIShare = menu.findItem(R.id.menu_item_share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idCapitulo = item.getItemId();
        try {

            //shared
            if (idCapitulo == R.id.menu_item_share) {
                prepareShareIntent();
            }
            if (idCapitulo == R.id.menu_item_search) {
                pesquisaLivro();
            }
            if (idCapitulo == R.id.sobre) {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
                //builderSingle.setIcon(R.drawable.ic_menu_search);
                builderSingle.setTitle("Sobre");
                builderSingle.setMessage("JmsApplay \nVersion: Biblia 1.0");
                builderSingle.setNeutralButton("Ok", null);
                builderSingle.show();

            } else if (idCapitulo > 0) {
                textToShared = "";
                ArrayList<String> lines = new ArrayList<String>();
                for (Capitulo cap : capitulos) {
                    if (idCapitulo == cap.getCapitulo()) {
                        strCapitulo = idCapitulo + "";
                        lines.add("Capítulo: " + idCapitulo);
                        for (Versiculo ver : capitulos.get(idCapitulo - 1).getVersiculos()) {
                            lines.add(ver.getTextoVersiculo());
                        }
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rom, R.id.txtVersiculo, lines);
                lv.setAdapter(adapter);

                return true;
            }
        } catch (Exception ex) {
            Log.e("Erro: ", ex.getLocalizedMessage());
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        try {
            //change background
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
            relativeLayout.setBackgroundResource(R.color.colorfundo);

            int id = item.getItemId();

            //share
            if (id == R.id.menu_item_share) {
                // Fetch and store ShareActionProvider
                mShareActionProvider = (ShareActionProvider) item.getActionProvider();
                StartFragment startFragment = new StartFragment();
                startFragment.getActivity().getSupportFragmentManager().popBackStack();


            } else {


                textToShared = "";


                if (id == R.id.nav_genesis) {
                    livro = "genesis";
                    tituloLivro = "Genesis";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_exodo) {
                    livro = "exodo";
                    tituloLivro = "Exodo";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();

                } else if (id == R.id.nav_levitico) {
                    livro = "levitico";
                    tituloLivro = "Levitico";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();

                    btnFrases.setVisibility(View.INVISIBLE);
                } else if (id == R.id.nav_deuteronomio) {
                    livro = "deuteronomio";
                    tituloLivro = "Deuteronomio";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();

                } else if (id == R.id.nav_numeros) {
                    livro = "numeros";
                    tituloLivro = "Numeros";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_josue) {
                    livro = "josue";
                    tituloLivro = "Josue";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_juizes) {
                    livro = "juizes";
                    tituloLivro = "Juizes";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_rute) {
                    livro = "rute";
                    tituloLivro = "Rute";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_isamuel) {
                    livro = "isamuel";
                    tituloLivro = "I Samuel";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_iisamuel) {
                    livro = "iisamuel";
                    tituloLivro = "II Samuel";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_ireis) {
                    livro = "ireis";
                    tituloLivro = "I Reis";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_iireis) {
                    livro = "iireis";
                    tituloLivro = "II Reis";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_icronicas) {
                    livro = "icronicas";
                    tituloLivro = "I Cronicas";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_iicronicas) {
                    livro = "iicronicas";
                    tituloLivro = "II Cronicas";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_esdras) {
                    livro = "esdras";
                    tituloLivro = "Esdras";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_neemias) {
                    livro = "neemias";
                    tituloLivro = "Neemias";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_ester) {
                    livro = "ester";
                    tituloLivro = "Ester";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_jo) {
                    livro = "jo";
                    tituloLivro = "Jo";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_salmos) {
                    livro = "salmos";
                    tituloLivro = "Salmos";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_proverbios) {
                    livro = "proverbios";
                    tituloLivro = "Proverbios";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_eclesiastes) {
                    livro = "eclesiastes";
                    tituloLivro = "Proverbios";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_canticos) {
                    livro = "canticosdoscanticos";
                    tituloLivro = "Canticos dos canticos";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_isaias) {
                    livro = "isaias";
                    tituloLivro = "Isaias";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_jeremias) {
                    livro = "jeremias";
                    tituloLivro = "Jeremias";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_lamentacoes) {
                    livro = "lamentacoes";
                    tituloLivro = "Lamentacoes";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_ezequiel) {
                    livro = "ezequiel";
                    tituloLivro = "Ezequiel";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_daniel) {
                    livro = "daniel";
                    tituloLivro = "Daniel";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_oseias) {
                    livro = "oseias";
                    tituloLivro = "Oseias";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_joel) {
                    livro = "joel";
                    tituloLivro = "Joel";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_obadias) {
                    livro = "obadias";
                    tituloLivro = "Obadias";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_jonas) {
                    livro = "jonas";
                    tituloLivro = "Jonas";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_miqueias) {
                    livro = "miqueias";
                    tituloLivro = "Miqueias";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_naum) {
                    livro = "naum";
                    tituloLivro = "Naum";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_habacuque) {
                    livro = "habacuque";
                    tituloLivro = "Habacuque";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_sofonias) {
                    livro = "sofonias";
                    tituloLivro = "Sofonias";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_ageu) {
                    livro = "ageu";
                    tituloLivro = "Ageu";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_zacarias) {
                    livro = "zacarias";
                    tituloLivro = "Zacarias";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_malaquias) {
                    livro = "malaquias";
                    tituloLivro = "Zalaquias";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_amos) {
                    livro = "amos";
                    tituloLivro = "Amos";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_mateus) {
                    livro = "mateus";
                    tituloLivro = "Mateus";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_marcos) {
                    livro = "marcos";
                    tituloLivro = "Marcos";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_lucas) {
                    livro = "lucas";
                    tituloLivro = "Lucas";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_joao) {
                    livro = "joao";
                    tituloLivro = "Joao";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_atos) {
                    livro = "atos";
                    tituloLivro = "Atos";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_romanos) {
                    livro = "romanos";
                    tituloLivro = "Romanos";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_icorintios) {
                    livro = "icorintios";
                    tituloLivro = "I Corintios";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_iicorintios) {
                    livro = "iicorintios";
                    tituloLivro = "II Corintios";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_galatas) {
                    livro = "galatas";
                    tituloLivro = "Galatas";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_efesios) {
                    livro = "efesios";
                    tituloLivro = "Efesios";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_filipenses) {
                    livro = "filipenses";
                    tituloLivro = "Filipenses";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_colossenses) {
                    livro = "colossenses";
                    tituloLivro = "Colossenses";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_itessalonicenses) {
                    livro = "itessalonicenses";
                    tituloLivro = "I Tessalonicenses";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_iitessalonicenses) {
                    livro = "iitessalonicenses";
                    tituloLivro = "II Tessalonicenses";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_itimoteo) {
                    livro = "itimoteo";
                    tituloLivro = "I Timoteo";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_iitimoteo) {
                    livro = "iitimoteo";
                    tituloLivro = "II Timoteo";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_tito) {
                    livro = "tito";
                    tituloLivro = "Tito";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_filemom) {
                    livro = "filemom";
                    tituloLivro = "Filemom";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_hebreus) {
                    livro = "hebreus";
                    tituloLivro = "Hebreus";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_tiago) {
                    livro = "tiago";
                    tituloLivro = "Tiago";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_ipedro) {
                    livro = "ipedro";
                    tituloLivro = "I Pedro";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_iipedro) {
                    livro = "iipedro";
                    tituloLivro = "II Pedro";

                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_ijoao) {
                    livro = "ijoao";
                    tituloLivro = "I Joao";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_iijoao) {
                    livro = "iijoao";
                    tituloLivro = "II Joao";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_iiijoao) {
                    livro = "iiijoao";
                    tituloLivro = "III Joao";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_judas) {
                    livro = "judas";
                    tituloLivro = "Judas";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                } else if (id == R.id.nav_apocalipse) {
                    livro = "apocalipse";
                    tituloLivro = "Apocalipse";
                    setTitle("Biblia - " + tituloLivro);
                    limpar();
                }


                preencheLista(livro);

                ArrayList<String> lines = new ArrayList<String>();

                lines.add("Capítulo: 1");
                for (Versiculo ver : capitulos.get(0).getVersiculos()) {
                    lines.add(ver.getTextoVersiculo());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rom, R.id.txtVersiculo, lines);
                lv.setAdapter(adapter);

                drawer.closeDrawer(GravityCompat.START);

                this.setTitle(item.getTitle());
            }
        } catch (Exception ex) {
            Log.e("Error, ", ex.getLocalizedMessage());
        }
        return true;
    }


    /*metodo com dialogo para fazer o dowloand
      *da biblia
*/
    @Override
    protected Dialog onCreateDialog(int id) {
        if (PanelDialog) {
            switch (id) {
                case progress_bar_type: // we set this to 0
                    pDialog = new ProgressDialog(this);
                    pDialog.setTitle("Um momento");
                    pDialog.setMessage("Carregando as palavras....");
                    pDialog.setIndeterminate(false);
                    pDialog.setMax(66);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    return pDialog;
                default:
                    return null;
            }
        }
        return null;
    }
    // fim dowloand


    private void cleanSharedItem() {
        textToShared = "";
    }

    // Gets the image URI and setup the associated share intent to hook into the provider
    public void prepareShareIntent() {

        if (textToShared.isEmpty()) {
            Toast.makeText(this, "Selecione um texto para compartihar", Toast.LENGTH_SHORT).show();
        } else {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, textToShared);
            startActivity(Intent.createChooser(sharingIntent, "Pra quem vai a palavra?"));
        }
    }

    private void preencheLista(String livro) {

        try {
            capitulos = new ArrayList<Capitulo>();
            String filePath = getExternalCacheDir() + "/BibliaSagrada/" + livro + ".txt";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"), 100);
            int idVersiculo = 1, idCapitulo = 1;

            Capitulo capitulo = null;
            Versiculo versiculo = null;

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {

                    if (line.startsWith("»")) {

                        if (capitulo != null) capitulos.add(capitulo);

                        line = line.replace("[", "").replace("]", "").replace("»", "");
                        capitulo = new Capitulo();

                        capitulo.setCapitulo(idCapitulo);
                        idCapitulo++;

                    } else {

                        if (capitulo != null) {

                            versiculo = new Versiculo();
                            versiculo.setTextoVersiculo(line);
                            versiculo.setVersiculo(idVersiculo);

                            capitulo.addVersiculo(versiculo);
                            idVersiculo++;
                        }
                    }
                }
            }

            if (capitulo != null) {

                capitulos.add(capitulo);
                strCapitulo = "" + idCapitulo;
            }

            br.close();


        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir o livro.", Toast.LENGTH_SHORT).show();
        }
    }

    private void insereMenuItemShare(Menu menu) {
        menu.add(0, R.id.menu_item_share, 0, "").setIcon(R.drawable.ic_menu_share)
                .setVisible(false)
                .setTitle("Enviar")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    private void insereCapitulos(int qtdeCap, Menu menu) {

        for (int i = 1; i <= qtdeCap; i++) {
            menu.add(0, i, i, "Capítulo " + i);
        }
    }

    private int QtdeCapituloDoLivro(String param) {

        int qtdeCap = 0;
        switch (param) {
            case "genesis":
                qtdeCap = 50;
                break;
            case "exodo":
                qtdeCap = 40;
                break;
            case "levitico":
                qtdeCap = 27;
                break;
            case "numeros":
                qtdeCap = 36;
                break;
            case "deuteronomio":
                qtdeCap = 34;
                break;
            case "josue":
                qtdeCap = 24;
                break;
            case "juizes":
                qtdeCap = 21;
                break;
            case "rute":
                qtdeCap = 4;
                break;
            case "isamuel":
                qtdeCap = 31;
                break;
            case "samuel":
                qtdeCap = 32;
                break;
            case "ireis":
                qtdeCap = 22;
                break;
            case "iireis":
                qtdeCap = 25;
                break;
            case "icronicas":
                qtdeCap = 29;
                break;
            case "iicronicas":
                qtdeCap = 36;
                break;
            case "esdras":
                qtdeCap = 10;
                break;
            case "neemias":
                qtdeCap = 13;
                break;
            case "ester":
                qtdeCap = 10;
                break;
            case "jo":
                qtdeCap = 42;
                break;
            case "salmos":
                qtdeCap = 150;
                break;
            case "proverbios":
                qtdeCap = 31;
                break;
            case "eclesiastes":
                qtdeCap = 12;
                break;
            case "cantares":
                qtdeCap = 8;
                break;
            case "isaias":
                qtdeCap = 66;
                break;
            case "jeremias":
                qtdeCap = 66;
                break;
            case "lamentacoes":
                qtdeCap = 5;
                break;
            case "ezequiel":
                qtdeCap = 48;
                break;
            case "daniel":
                qtdeCap = 12;
                break;
            case "oseias":
                qtdeCap = 14;
                break;
            case "joel":
                qtdeCap = 3;
                break;
            case "amos":
                qtdeCap = 9;
                break;
            case "obadias":
                qtdeCap = 1;
                break;
            case "jonas":
                qtdeCap = 4;
                break;
            case "miqueias":
                qtdeCap = 7;
                break;
            case "naum":
                qtdeCap = 3;
                break;
            case "habacuque":
                qtdeCap = 3;
                break;
            case "sofonias":
                qtdeCap = 3;
                break;
            case "ageu":
                qtdeCap = 2;
                break;
            case "zacarias":
                qtdeCap = 14;
                break;
            case "malaquias":
                qtdeCap = 4;
                break;
            case "mateus":
                qtdeCap = 28;
                break;
            case "marcos":
                qtdeCap = 16;
                break;
            case "lucas":
                qtdeCap = 24;
                break;
            case "joao":
                qtdeCap = 21;
                break;
            case "atos":
                qtdeCap = 28;
                break;
            case "romanos":
                qtdeCap = 16;
                break;
            case "icorintios":
                qtdeCap = 16;
                break;
            case "iicorintios":
                qtdeCap = 13;
                break;
            case "galatas":
                qtdeCap = 6;
                break;
            case "efesios":
                qtdeCap = 6;
                break;
            case "filipenses":
                qtdeCap = 4;
                break;
            case "colossenses":
                qtdeCap = 4;
                break;
            case "itessalonicenses":
                qtdeCap = 5;
                break;
            case "iitessalonicenses":
                qtdeCap = 3;
                break;
            case "itimoteo":
                qtdeCap = 6;
                break;
            case "iitimoteo":
                qtdeCap = 4;
                break;
            case "tito":
                qtdeCap = 3;
                break;
            case "filemon":
                qtdeCap = 1;
                break;
            case "hebreus":
                qtdeCap = 13;
                break;
            case "tiago":
                qtdeCap = 5;
                break;
            case "ipedro":
                qtdeCap = 5;
                break;
            case "iipedro":
                qtdeCap = 3;
                break;
            case "ijoao":
                qtdeCap = 5;
                break;
            case "iijoao":
                qtdeCap = 1;
                break;
            case "iiijoao":
                qtdeCap = 1;
                break;
            case "judas":
                qtdeCap = 1;
                break;
            case "apocalipse":
                qtdeCap = 22;
                break;
            default:
                break;
        }

        return qtdeCap;
    }

    private void prepare() {
        try {

            //File file = new File(Environment.getExternalStorageDirectory() + "/BibliaSagrada/");
            File file = new File(getExternalCacheDir(), "BibliaSagrada");

            if (!file.exists())
                if (!file.mkdirs())
                    Log.e("Error: ,", "erro para criar o diretório");

            if (file.listFiles().length < 66) {

                if (!isOnline()) {
                    Toast.makeText(this, "Primeiro acesso ao aplicativo presica de acesso a internet para baixar a biblia.", Toast.LENGTH_LONG).show();
                    return;
                }
                new DownloadFileFromURL().execute(file_url);
            } else {
                // dismiss the dialog after the file was downloaded
                PanelDialog = false;
                //if (pDialog != null) progress_bar_type = 66; //pDialog.dismiss();
                //pDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    private void mensagemSelecionado() {
        Toast.makeText(this, "Selecionado!", Toast.LENGTH_SHORT).show();

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void pesquisaLivro() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        //builderSingle.setIcon(R.drawable.ic_menu_search);
        builderSingle.setTitle("Informe o texto:");

        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        //arrayAdapter.add("Hardik");
        //arrayAdapter.add("Archit");
        //arrayAdapter.add("Jignesh");
        //arrayAdapter.add("Umang");
        //arrayAdapter.add("Gatti");
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builderSingle.setView(input);


        builderSingle.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setPositiveButton("Buscar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        buscarTexto(input.getText().toString());
                    }
                });

        /*builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });*/
        builderSingle.show();
    }

    public void buscarTexto(String texto) {

        ArrayList<String> resultadosEncontrados = new ArrayList<String>();

        for (String livroInPesquisa : getLivros()) {
            try {

                String filePath = getExternalCacheDir() + "/BibliaSagrada/" + livroInPesquisa + ".txt";
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"), 100);
                int idVersiculo = 1, idCapitulo = 1;

                String capitulo = "", linha, line;

                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        if (line.startsWith("»")) {
                            capitulo = livroInPesquisa + " - " + idVersiculo + " - " + idCapitulo + " - " + line.replace("[", "").replace("]", "").replace("»", "");
                            idCapitulo++;
                        } else if (line.indexOf(texto) > -1) {
                            resultadosEncontrados.add(capitulo + line);
                            /*if (capitulo != null) {
                                versiculo = new Versiculo();
                                versiculo.setTextoVersiculo(line);
                                versiculo.setVersiculo(idVersiculo);

                                capitulo.addVersiculo(versiculo);
                                idVersiculo++;
                            }*/
                        }
                    }

                    if (resultadosEncontrados.size() == 10) {
                        break;
                    }
                }
                br.close();
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao abrir o livro. Se o erro persistir, reinicie o app e tente novamente.", Toast.LENGTH_SHORT).show();
            }

            if (resultadosEncontrados.size() == 10) {
                break;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rom, R.id.txtVersiculo, resultadosEncontrados);
        lv.setAdapter(adapter);
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count = 0;
            try {
                for (String book : getLivros()) {

                    book += ".txt";

                    File txtToWrite = new File(getExternalCacheDir(), "BibliaSagrada/" + book);//new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BibliaSagrada/" + book);
                    FileWriter writer = new FileWriter(txtToWrite);

                    URL url = new URL(f_url[0] + "/" + book);
                    URLConnection conection = url.openConnection();
                    conection.connect();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));


                    String str;
                    while ((str = reader.readLine()) != null) {
                        writer.write(str + "\n\t");
                    }
                    reader.close();

                    writer.flush();
                    writer.close();

                    count++;
                    publishProgress("" + count);
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
        }

    }

    private String[] Livros;

    public String[] getLivros() {
        return new String[]{
                "genesis",
                "exodo",
                "levitico",
                "numeros",
                "deuteronomio",
                "josue",
                "juizes",
                "rute",
                "isamuel",
                "iisamuel",
                "ireis",
                "iireis",
                "icronicas",
                "iicronicas",
                "esdras",
                "neemias",
                "ester",
                "jo",
                "salmos",
                "proverbios",
                "eclesiastes",
                "canticosdoscanticos",
                "isaias",
                "jeremias",
                "lamentacoes",
                "ezequiel",
                "daniel",
                "oseias",
                "joel",
                "obadias",
                "jonas",
                "miqueias",
                "naum",
                "habacuque",
                "sofonias",
                "ageu",
                "zacarias",
                "amos",
                "malaquias",
                "mateus",
                "marcos",
                "lucas",
                "joao",
                "atos",
                "romanos",
                "icorintios",
                "iicorintios",
                "galatas",
                "efesios",
                "filipenses",
                "colossenses",
                "itessalonicenses",
                "iitessalonicenses",
                "itimoteo",
                "iitimoteo",
                "tito",
                "filemom",
                "hebreus",
                "tiago",
                "ipedro",
                "iipedro",
                "ijoao",
                "iijoao",
                "iiijoao",
                "judas",
                "apocalipse"

        };

    }
        String[] Palavras;
        public String[] getPalavras() {
            return new String[]{
                    "Genesis",
                    "Exodo",
                    "Levitico",
                    "Numeros",
                    "Deuteronomio",
                    "Josue",
                    "Juizes",
                    "Rute",
                    "Isamuel",
                    "II samuel",
                    "I Reis",
                    "II reis",
                    "I Cronicas",
                    "II Cronicas",
                    "Esdras",
                    "Neemias",
                    "Ester",
                    "Jo",
                    "Salmos",
                    "Proverbios",
                    "Eclesiastes",
                    "Canticosdoscanticos",
                    "Isaias",
                    "Jeremias",
                    "Lamentacoes",
                    "Ezequiel",
                    "Daniel",
                    "Oseias",
                    "Joel",
                    "Obadias",
                    "Jonas",
                    "Miqueias",
                    "Naum",
                    "Habacuque",
                    "Sofonias",
                    "Ageu",
                    "Zacarias",
                    "Amos",
                    "Malaquias",
                    "Mateus",
                    "Marcos",
                    "Lucas",
                    "Joao",
                    "Atos",
                    "Romanos",
                    "I Corintios",
                    "II Corintios",
                    "Galatas",
                    "Efesios",
                    "Filipenses",
                    "Colossenses",
                    "I Tessalonicenses",
                    "II Tessalonicenses",
                    "I Timoteo",
                    "II Timoteo",
                    "Tito",
                    "Filemom",
                    "Hebreus",
                    "Tiago",
                    "I Pedro",
                    "II Pedro",
                    "I Joao",
                    "II Joao",
                    "III Joao",
                    "Judas",
                    "Apocalipse"


            };
        }

    private  void limpar(){

        txtPalavras.setVisibility(View.INVISIBLE);
        btnFrases.setVisibility(View.INVISIBLE);
    }
    private  void limparVoltar(){

        txtPalavras.setVisibility(View.VISIBLE);
        btnFrases.setVisibility(View.VISIBLE);
    }


}
