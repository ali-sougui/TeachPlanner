PGDMP  &    .            
    |            postgres    12.20    16.4 (    H           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            I           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            J           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            K           1262    13318    postgres    DATABASE     {   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'French_France.1252';
    DROP DATABASE postgres;
                postgres    false            L           0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                   postgres    false    2891                        2615    2200    public    SCHEMA     2   -- *not* creating schema, since initdb creates it
 2   -- *not* dropping schema, since initdb creates it
                postgres    false            M           0    0    SCHEMA public    ACL     Q   REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;
                   postgres    false    7                        3079    16384 	   adminpack 	   EXTENSION     A   CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;
    DROP EXTENSION adminpack;
                   false            N           0    0    EXTENSION adminpack    COMMENT     M   COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';
                        false    1            y           1247    16403 
   type_cours    TYPE     H   CREATE TYPE public.type_cours AS ENUM (
    'CM',
    'TP',
    'TD'
);
    DROP TYPE public.type_cours;
       public          postgres    false    7                       1247    16418 
   type_dispo    TYPE     q   CREATE TYPE public.type_dispo AS ENUM (
    'Lundi',
    'Mardi',
    'Mercredi',
    'Jeudi',
    'Vendredi'
);
    DROP TYPE public.type_dispo;
       public          postgres    false    7            �           1247    16430 	   type_pref    TYPE     W   CREATE TYPE public.type_pref AS ENUM (
    'Matin',
    'Soir',
    'Matin_et_Soir'
);
    DROP TYPE public.type_pref;
       public          postgres    false    7            |           1247    16410 
   type_salle    TYPE     W   CREATE TYPE public.type_salle AS ENUM (
    'Amphi',
    'salle_tp',
    'salle_td'
);
    DROP TYPE public.type_salle;
       public          postgres    false    7            �            1259    16470    Administrateurs    TABLE     �   CREATE TABLE public."Administrateurs" (
    id_admin integer NOT NULL,
    nom_admin character varying(50) NOT NULL,
    prenom_admin character varying(50) NOT NULL,
    idperso integer
);
 %   DROP TABLE public."Administrateurs";
       public         heap    postgres    false    7            �            1259    16518    Departements    TABLE     x   CREATE TABLE public."Departements" (
    id_departement integer NOT NULL,
    nom_departement character varying(100)
);
 "   DROP TABLE public."Departements";
       public         heap    postgres    false    7            �            1259    16536    Disponibilite    TABLE     �   CREATE TABLE public."Disponibilite" (
    id_disponibilite integer NOT NULL,
    jour public.type_dispo NOT NULL,
    heure_debut time with time zone,
    heure_fin time with time zone
);
 #   DROP TABLE public."Disponibilite";
       public         heap    postgres    false    7    639            �            1259    16480    Enseignants    TABLE     �   CREATE TABLE public."Enseignants" (
    numero_ens integer NOT NULL,
    nom_ens character varying(50) NOT NULL,
    prenom_ens character varying(50) NOT NULL,
    idperso integer
);
 !   DROP TABLE public."Enseignants";
       public         heap    postgres    false    7            �            1259    16524    Groupes    TABLE     �   CREATE TABLE public."Groupes" (
    num_groupe integer DEFAULT 0 NOT NULL,
    type_cours public.type_cours NOT NULL,
    code_matiere character varying NOT NULL,
    nom_groupe character varying(50)
);
    DROP TABLE public."Groupes";
       public         heap    postgres    false    633    7            �            1259    16394    Matieres    TABLE     �   CREATE TABLE public."Matieres" (
    nom_matiere character varying NOT NULL,
    code_matiere character varying(50) NOT NULL
);
    DROP TABLE public."Matieres";
       public         heap    postgres    false    7            �            1259    16455 
   Personnels    TABLE     �   CREATE TABLE public."Personnels" (
    idperso integer NOT NULL,
    mail character varying(50),
    mdp character varying(20)
);
     DROP TABLE public."Personnels";
       public         heap    postgres    false    7            �            1259    16447    Salles    TABLE     �   CREATE TABLE public."Salles" (
    numero_salle character varying(50) NOT NULL,
    nom_salle character varying(50),
    type_salle public.type_salle
);
    DROP TABLE public."Salles";
       public         heap    postgres    false    7    636            �            1259    16452    Seances    TABLE       CREATE TABLE public."Seances" (
    heure_debut time without time zone NOT NULL,
    heure_fin time without time zone NOT NULL,
    date date NOT NULL,
    numero_ens integer NOT NULL,
    numero_salle character varying(50) NOT NULL,
    code_matiere character varying(50) NOT NULL
);
    DROP TABLE public."Seances";
       public         heap    postgres    false    7            �            1259    16516    departements_id_departement_seq    SEQUENCE     �   CREATE SEQUENCE public.departements_id_departement_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 6   DROP SEQUENCE public.departements_id_departement_seq;
       public          postgres    false    7    210            O           0    0    departements_id_departement_seq    SEQUENCE OWNED BY     e   ALTER SEQUENCE public.departements_id_departement_seq OWNED BY public."Departements".id_departement;
          public          postgres    false    209            �
           2604    16521    Departements id_departement    DEFAULT     �   ALTER TABLE ONLY public."Departements" ALTER COLUMN id_departement SET DEFAULT nextval('public.departements_id_departement_seq'::regclass);
 L   ALTER TABLE public."Departements" ALTER COLUMN id_departement DROP DEFAULT;
       public          postgres    false    209    210    210            �
           2606    16540     Disponibilite Disponibilite_pkey 
   CONSTRAINT     v   ALTER TABLE ONLY public."Disponibilite"
    ADD CONSTRAINT "Disponibilite_pkey" PRIMARY KEY (id_disponibilite, jour);
 N   ALTER TABLE ONLY public."Disponibilite" DROP CONSTRAINT "Disponibilite_pkey";
       public            postgres    false    212    212            �
           2606    16552    Groupes Groupes_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY public."Groupes"
    ADD CONSTRAINT "Groupes_pkey" PRIMARY KEY (code_matiere, num_groupe, type_cours);
 B   ALTER TABLE ONLY public."Groupes" DROP CONSTRAINT "Groupes_pkey";
       public            postgres    false    211    211    211            �
           2606    16508    Matieres Matiere_pkey 
   CONSTRAINT     a   ALTER TABLE ONLY public."Matieres"
    ADD CONSTRAINT "Matiere_pkey" PRIMARY KEY (code_matiere);
 C   ALTER TABLE ONLY public."Matieres" DROP CONSTRAINT "Matiere_pkey";
       public            postgres    false    203            �
           2606    16474 #   Administrateurs administrateur_pkey 
   CONSTRAINT     i   ALTER TABLE ONLY public."Administrateurs"
    ADD CONSTRAINT administrateur_pkey PRIMARY KEY (id_admin);
 O   ALTER TABLE ONLY public."Administrateurs" DROP CONSTRAINT administrateur_pkey;
       public            postgres    false    207            �
           2606    16523    Departements departements_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY public."Departements"
    ADD CONSTRAINT departements_pkey PRIMARY KEY (id_departement);
 J   ALTER TABLE ONLY public."Departements" DROP CONSTRAINT departements_pkey;
       public            postgres    false    210            �
           2606    16459    Personnels personnels_pkey 
   CONSTRAINT     _   ALTER TABLE ONLY public."Personnels"
    ADD CONSTRAINT personnels_pkey PRIMARY KEY (idperso);
 F   ALTER TABLE ONLY public."Personnels" DROP CONSTRAINT personnels_pkey;
       public            postgres    false    206            �
           2606    16484    Enseignants professeurs_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public."Enseignants"
    ADD CONSTRAINT professeurs_pkey PRIMARY KEY (numero_ens);
 H   ALTER TABLE ONLY public."Enseignants" DROP CONSTRAINT professeurs_pkey;
       public            postgres    false    208            �
           2606    16451    Salles salles_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public."Salles"
    ADD CONSTRAINT salles_pkey PRIMARY KEY (numero_salle);
 >   ALTER TABLE ONLY public."Salles" DROP CONSTRAINT salles_pkey;
       public            postgres    false    204            �
           2606    16530    Seances seances_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY public."Seances"
    ADD CONSTRAINT seances_pkey PRIMARY KEY (heure_debut, heure_fin, date, numero_ens, numero_salle, code_matiere);
 @   ALTER TABLE ONLY public."Seances" DROP CONSTRAINT seances_pkey;
       public            postgres    false    205    205    205    205    205    205            �
           2606    16475 +   Administrateurs administrateur_idperso_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."Administrateurs"
    ADD CONSTRAINT administrateur_idperso_fkey FOREIGN KEY (idperso) REFERENCES public."Personnels"(idperso);
 W   ALTER TABLE ONLY public."Administrateurs" DROP CONSTRAINT administrateur_idperso_fkey;
       public          postgres    false    2743    206    207            �
           2606    16553    Groupes fk_code_matiere    FK CONSTRAINT     �   ALTER TABLE ONLY public."Groupes"
    ADD CONSTRAINT fk_code_matiere FOREIGN KEY (code_matiere) REFERENCES public."Matieres"(code_matiere) NOT VALID;
 C   ALTER TABLE ONLY public."Groupes" DROP CONSTRAINT fk_code_matiere;
       public          postgres    false    2737    203    211            �
           2606    16490    Seances fk_ens    FK CONSTRAINT     �   ALTER TABLE ONLY public."Seances"
    ADD CONSTRAINT fk_ens FOREIGN KEY (numero_ens) REFERENCES public."Enseignants"(numero_ens);
 :   ALTER TABLE ONLY public."Seances" DROP CONSTRAINT fk_ens;
       public          postgres    false    2747    205    208            �
           2606    16485    Enseignants fk_id_perso    FK CONSTRAINT     �   ALTER TABLE ONLY public."Enseignants"
    ADD CONSTRAINT fk_id_perso FOREIGN KEY (idperso) REFERENCES public."Personnels"(idperso);
 C   ALTER TABLE ONLY public."Enseignants" DROP CONSTRAINT fk_id_perso;
       public          postgres    false    2743    208    206            �
           2606    16500    Seances fk_numero_salle    FK CONSTRAINT     �   ALTER TABLE ONLY public."Seances"
    ADD CONSTRAINT fk_numero_salle FOREIGN KEY (numero_salle) REFERENCES public."Salles"(numero_salle);
 C   ALTER TABLE ONLY public."Seances" DROP CONSTRAINT fk_numero_salle;
       public          postgres    false    2739    204    205            �
           2606    16531 !   Seances seances_code_matiere_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."Seances"
    ADD CONSTRAINT seances_code_matiere_fkey FOREIGN KEY (code_matiere) REFERENCES public."Matieres"(code_matiere) NOT VALID;
 M   ALTER TABLE ONLY public."Seances" DROP CONSTRAINT seances_code_matiere_fkey;
       public          postgres    false    205    2737    203           