package com.prograavanzada.recordatoriodemedicina;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MedicinaAdapter extends RecyclerView.Adapter<MedicinaAdapter.MedicinaViewHolder> {

    private List<Medicina> listaMedicinas = new ArrayList<>();
    private OnItemClickListener listener;

    // ¡NUEVO! Ahora la interfaz tiene dos acciones separadas
    public interface OnItemClickListener {
        void onEditClick(Medicina medicina);
        void onDeleteClick(Medicina medicina);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setMedicinas(List<Medicina> medicinas) {
        this.listaMedicinas = medicinas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicina, parent, false);
        return new MedicinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicinaViewHolder holder, int position) {
        Medicina medicinaActual = listaMedicinas.get(position);

        holder.tvNombre.setText(medicinaActual.nombre);
        holder.tvDosis.setText("Dosis: " + medicinaActual.cantidad);
        // Asignamos el texto base
        holder.tvInventario.setText("Inventario: " + medicinaActual.inventarioTotal);

        // --- ¡NUEVA LÓGICA DE ALERTA VISUAL! ---
        if (medicinaActual.inventarioTotal <= 0) {
            // Si se acabó, ponemos el texto de alerta en tu color rojo oscuro
            holder.tvInventario.setTextColor(android.graphics.Color.parseColor("#D32F2F"));
            holder.tvInventario.setText("Inventario: 0 (¡AGOTADO!)");
            // Opcional: Puedes hacer la letra negrita para que resalte más
            holder.tvInventario.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            // Si aún hay pastillas, lo regresamos a su color gris/normal
            holder.tvInventario.setTextColor(android.graphics.Color.parseColor("#AAAAAA")); // Usa el color normal que tenías
            // Quitamos la negrita
            holder.tvInventario.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        holder.tvHora.setText(medicinaActual.horaAlarma);
    }

    @Override
    public int getItemCount() {
        return listaMedicinas.size();
    }

    class MedicinaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDosis, tvInventario, tvHora;
        ImageButton btnEdit, btnDelete; // Agregamos los botones

        public MedicinaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvItemNombre);
            tvDosis = itemView.findViewById(R.id.tvItemDosis);
            tvInventario = itemView.findViewById(R.id.tvItemInventario);
            tvHora = itemView.findViewById(R.id.tvItemHora);

            // Enlazamos los botones
            btnEdit = itemView.findViewById(R.id.btnEditItem);
            btnDelete = itemView.findViewById(R.id.btnDeleteItem);

            // Configuramos qué pasa al tocar el botón EDITAR
            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(listaMedicinas.get(position));
                }
            });

            // Configuramos qué pasa al tocar el botón ELIMINAR
            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(listaMedicinas.get(position));
                }
            });
        }
    }
}